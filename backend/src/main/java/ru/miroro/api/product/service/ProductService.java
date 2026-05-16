package ru.miroro.api.product.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.miroro.api.product.dto.ProductDto;
import ru.miroro.api.product.dto.VariantDto;
import ru.miroro.api.product.mapper.ProductMapper;
import ru.miroro.api.product.model.Image;
import ru.miroro.api.product.model.Product;
import ru.miroro.api.product.model.Variant;
import ru.miroro.api.product.repository.ImageRepository;
import ru.miroro.api.product.repository.ProductRepository;
import ru.miroro.api.product.repository.VariantRepository;

@RequiredArgsConstructor
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;
    private final ImageRepository imageRepository;
    private final ProductMapper productMapper;

    @Value("${app.upload.img-path}")
    private String imageDir;

    // =====================================================
    // READ
    // =====================================================

    @Transactional(readOnly = true)
    public List<ProductDto> findAll() {
        return productRepository.findAllByOrderByIdAsc().stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDto findById(int id) {
        return productRepository.findById(id).map(productMapper::toDto).orElse(null);
    }

    @Transactional(readOnly = true)
    public ProductDto getMostExpensiveProduct() {
        return productRepository
                .findMostExpensiveProduct()
                .map(productMapper::toDto)
                .orElse(null);
    }

    // =====================================================
    // CREATE
    // =====================================================

    public ProductDto create(ProductDto dto, List<MultipartFile> imageFiles) throws IOException {

        Product product = productMapper.toEntity(dto);
        Product saved = productRepository.save(product);

        // =====================================================
        // VARIANTS
        // =====================================================
        if (dto.getVariants() != null && !dto.getVariants().isEmpty()) {

            List<Variant> variants = dto.getVariants().stream()
                    .map(v -> {
                        Variant variant = new Variant();
                        variant.setVariantId(null);
                        variant.setProduct(saved);
                        variant.setSizeId(v.getSizeId());
                        variant.setColorId(v.getColorId());
                        variant.setQuantity(v.getQuantity() == null ? 0 : v.getQuantity());
                        return variant;
                    })
                    .toList();

            variantRepository.saveAll(variants);
        }

        // =====================================================
        // IMAGES
        // =====================================================
        saveImages(saved, imageFiles, dto);

        return productRepository
                .findById(saved.getId())
                .map(productMapper::toDto)
                .orElse(null);
    }

    // =====================================================
    // UPDATE
    // =====================================================

    public int update(int id, ProductDto dto, List<MultipartFile> imageFiles) throws IOException {

        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return 0;
        }

        Product existing = optionalProduct.get();

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setSegmentId(dto.getSegmentId());

        productRepository.save(existing);

        // =====================================================
        // VARIANTS
        // =====================================================
        List<Variant> oldVariants = variantRepository.findByProductId(id);

        Set<String> oldKeys = new HashSet<>();
        for (Variant v : oldVariants) {
            oldKeys.add(key(v.getSizeId(), v.getColorId()));
        }

        Set<String> newKeys = new HashSet<>();
        Map<String, VariantDto> newMap = new HashMap<>();

        if (dto.getVariants() != null) {
            for (VariantDto v : dto.getVariants()) {
                String k = key(v.getSizeId(), v.getColorId());
                newKeys.add(k);
                newMap.put(k, v);
            }
        }

        for (Variant v : oldVariants) {
            String k = key(v.getSizeId(), v.getColorId());
            if (!newKeys.contains(k)) {
                variantRepository.delete(v);
            }
        }

        for (String k : newKeys) {
            if (!oldKeys.contains(k)) {

                VariantDto v = newMap.get(k);

                Variant variant = new Variant();
                variant.setVariantId(null);
                variant.setProduct(existing);
                variant.setSizeId(v.getSizeId());
                variant.setColorId(v.getColorId());
                variant.setQuantity(v.getQuantity() == null ? 0 : v.getQuantity());

                variantRepository.save(variant);
            }
        }

        // =====================================================
        // IMAGES
        // =====================================================
        if (imageFiles != null && !imageFiles.isEmpty()) {

            imageRepository.deleteByProductId(id);

            Path productDir = Paths.get(imageDir, "product_" + id);
            deleteDirectory(productDir);

            saveImages(existing, imageFiles, dto);
        }

        return 1;
    }

    // =====================================================
    // DELETE
    // =====================================================

    public int deleteById(int id) {

        productRepository.deleteById(id);

        try {
            Path productDir = Paths.get(imageDir, "product_" + id);
            deleteDirectory(productDir);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось удалить изображения товара", e);
        }

        return 1;
    }

    // =====================================================
    // IMAGES
    // =====================================================

    private void saveImages(Product product, List<MultipartFile> imageFiles, ProductDto dto) throws IOException {

        if (imageFiles == null || imageFiles.isEmpty()) {
            return;
        }

        String dir = "product_" + product.getId();

        for (int i = 0; i < imageFiles.size(); i++) {

            MultipartFile file = imageFiles.get(i);

            if (file == null || file.isEmpty()) {
                continue;
            }

            Integer colorId = null;

            if (dto.getImages() != null && i < dto.getImages().size()) {
                colorId = dto.getImages().get(i).getColorId();
            }

            String safe = sanitizeFileName(file.getOriginalFilename());

            Path path = Paths.get(imageDir, dir, safe);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            Image image = Image.builder()
                    .product(product)
                    .path("/img/" + dir + "/" + safe)
                    .colorId(colorId)
                    .isMain(false)
                    .build();

            imageRepository.save(image);
        }
    }

    // =====================================================
    // UTILS
    // =====================================================

    private String key(Integer sizeId, Integer colorId) {
        return sizeId + ":" + colorId;
    }

    private void deleteDirectory(Path dir) throws IOException {
        if (Files.exists(dir)) {
            Files.walk(dir).sorted(Comparator.reverseOrder()).forEach(p -> p.toFile()
                    .delete());
        }
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "";
        }

        return fileName.replaceAll("[^a-zA-Z0-9а-яА-ЯёЁ\\-_.]", "_")
                .replaceAll("_{2,}", "_")
                .trim();
    }
}
