package ru.miroro.api.product.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.miroro.api.product.dto.ProductDto;
import ru.miroro.api.product.dto.VariantDto;
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
    private final ConversionService conversionService;

    @Value("${app.upload.img-path}")
    private String imageDir;

    // =====================================================
    // READ
    // =====================================================

    @Transactional(readOnly = true)
    public List<ProductDto> findAll() {
        return productRepository.findAllByOrderByIdAsc().stream()
                .map(product -> conversionService.convert(product, ProductDto.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDto findById(int id) {

        return productRepository
                .findById(id)
                .map(product -> conversionService.convert(product, ProductDto.class))
                .orElse(null);
    }

    // =====================================================
    // CREATE
    // =====================================================

    public ProductDto create(ProductDto dto, List<MultipartFile> imageFiles) throws IOException {

        Product product = conversionService.convert(dto, Product.class);

        if (product == null) {
            return null;
        }

        Product saved = productRepository.save(product);

        // VARIANTS
        if (dto.getVariants() != null) {
            List<Variant> variants = dto.getVariants().stream()
                    .map(v -> {
                        Variant variant = new Variant();
                        variant.setVariantId(null);
                        variant.setQuantity(v.getQuantity() == null ? 0 : v.getQuantity());
                        variant.setProduct(saved);
                        variant.setSizeId(v.getSizeId());
                        variant.setColorId(v.getColorId());
                        return variant;
                    })
                    .toList();

            variantRepository.saveAll(variants);
        }

        saveImages(saved, imageFiles);

        return productRepository
                .findById(saved.getId())
                .map(p -> conversionService.convert(p, ProductDto.class))
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

        // VARIANTS
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

        // DELETE MISSING
        for (Variant v : oldVariants) {
            String k = key(v.getSizeId(), v.getColorId());

            if (!newKeys.contains(k)) {
                variantRepository.delete(v);
            }
        }

        // ADD NEW
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

        // IMAGES
        if (imageFiles != null && !imageFiles.isEmpty()) {

            imageRepository.deleteByProductId(id);

            Path productDir = Paths.get(imageDir, "product_" + id);

            deleteDirectory(productDir);

            saveImages(existing, imageFiles);
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
            throw new RuntimeException("message: Не удалось удалить изображения товара", e);
        }

        return 1;
    }

    // =====================================================
    // IMAGES
    // =====================================================

    private void saveImages(Product product, List<MultipartFile> imageFiles) throws IOException {

        if (imageFiles == null || imageFiles.isEmpty()) {
            return;
        }

        String dir = "product_" + product.getId();

        for (MultipartFile file : imageFiles) {

            if (file == null || file.isEmpty()) {
                continue;
            }

            String safe = sanitizeFileName(file.getOriginalFilename());

            Path path = Paths.get(imageDir, dir, safe);

            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            Image image = Image.builder()
                    .product(product)
                    .path("/img/" + dir + "/" + safe)
                    .isMain(false)
                    .build();

            imageRepository.save(image);
        }
    }

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
