package ru.miroro.api.product.service;

import java.io.File;
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
import ru.miroro.api.product.model.Image;
import ru.miroro.api.product.model.Product;
import ru.miroro.api.product.model.Variant;
import ru.miroro.api.product.repository.ProductRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository repo;

    @Value("${app.upload.img-path}")
    private String imageDir;

    // ------------------------------------------------
    // FIND ALL
    // ------------------------------------------------
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return repo.findAll();
    }

    // ------------------------------------------------
    // FIND BY ID
    // ------------------------------------------------
    @Transactional(readOnly = true)
    public Product findById(int id) {
        return repo.getProductById(id);
    }

    // ------------------------------------------------
    // CREATE
    // ------------------------------------------------
    public Product create(Product product, List<MultipartFile> imageFiles) throws IOException {

        // 1. создаём продукт
        Product created = repo.addProduct(product);
        int productId = created.getId();

        // имя директории продукта (стабильное)
        String productDirName = "product_" + productId;

        // 2. сохраняем варианты
        if (product.getVariants() != null) {
            for (Variant variant : product.getVariants()) {
                variant.setQuantity(0);
                repo.addVariant(productId, variant);
            }
        }

        // 3. сохраняем изображения
        saveImages(product, imageFiles, productId, productDirName);

        return repo.getProductById(productId);
    }

    // ------------------------------------------------
    // UPDATE
    // ------------------------------------------------
    public int update(int id, Product product, List<MultipartFile> imageFiles) throws IOException {

        // 1. обновляем базовые поля продукта
        int updatedRows = repo.updateProduct(id, product);
        if (updatedRows == 0) {
            return 0;
        }

        // =====================================================
        // VARIANT DIFF UPDATE
        // =====================================================

        // текущие вариации из БД
        Product existingProduct = repo.getProductById(id);

        Set<Variant> oldVariants =
                new HashSet<>(existingProduct.getVariants() == null ? List.of() : existingProduct.getVariants());

        Set<Variant> newVariants = new HashSet<>(product.getVariants() == null ? List.of() : product.getVariants());

        // -------- найти пересечения --------
        Set<Variant> intersection = new HashSet<>(oldVariants);
        intersection.retainAll(newVariants);

        // удалить совпадения
        oldVariants.removeAll(intersection);
        newVariants.removeAll(intersection);

        // -------- удалить старые --------
        for (Variant variantToDelete : oldVariants) {
            repo.removeVariant(id, variantToDelete);
        }

        // -------- добавить новые --------
        for (Variant variantToAdd : newVariants) {
            repo.addVariant(id, variantToAdd);
        }

        // =====================================================
        // IMAGES (оставляем прежнюю стратегию)
        // =====================================================

        if (imageFiles != null && !imageFiles.isEmpty()) {

            String productDirName = "product_" + id;

            repo.deleteImagesByProductId(id);

            Path productDir = Paths.get(imageDir, productDirName);
            deleteDirectory(productDir);

            saveImages(product, imageFiles, id, productDirName);
        }

        return updatedRows;
    }

    // ------------------------------------------------
    // DELETE
    // ------------------------------------------------
    public int deleteById(int id) {

        int deleted = repo.deleteById(id);

        if (deleted > 0) {
            try {
                String productDirName = "product_" + id;
                Path productDir = Paths.get(imageDir, productDirName);
                deleteDirectory(productDir);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete product images", e);
            }
        }

        return deleted;
    }

    // ============================================================
    // helpers (у тебя уже были — оставляю без изменений)
    // ============================================================

    private void saveImages(Product product, List<MultipartFile> imageFiles, int productId, String productDirName)
            throws IOException {

        if (imageFiles == null || imageFiles.isEmpty()) {
            return;
        }

        for (MultipartFile file : imageFiles) {
            if (file != null && !file.isEmpty() && file.getOriginalFilename() != null) {

                String safeFileName = sanitizeFileName(file.getOriginalFilename());

                Path filePath = Paths.get(imageDir, productDirName, safeFileName);

                Files.createDirectories(filePath.getParent());
                Files.write(filePath, file.getBytes());

                String dbPath = "/img/" + productDirName + "/" + safeFileName;

                Image sourceImage = product.getImages().stream()
                        .filter(img -> img.getPath().equals(file.getOriginalFilename()))
                        .findFirst()
                        .orElse(null);

                Image imageEntity = Image.builder()
                        .path(dbPath)
                        .isMain(sourceImage != null && Boolean.TRUE.equals(sourceImage.getIsMain()))
                        .colorId(sourceImage != null ? sourceImage.getColorId() : null)
                        .build();

                repo.addImage(productId, imageEntity);
            }
        }
    }

    private void deleteDirectory(Path dir) throws IOException {
        if (Files.exists(dir)) {
            Files.walk(dir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null) return "";
        return fileName.replaceAll("[^a-zA-Z0-9а-яА-ЯёЁ\\-_.]", "_")
                .replaceAll("_{2,}", "_")
                .trim();
    }
}
