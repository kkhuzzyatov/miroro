package ru.miroro.api.product.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.miroro.api.product.dto.ImageDto;
import ru.miroro.api.product.model.Image;

@Component
public class ImageToImageDtoConverter implements Converter<Image, ImageDto> {

    @Override
    public ImageDto convert(Image image) {

        if (image == null) {
            return null;
        }

        return ImageDto.builder()
                .imageId(image.getImageId())
                .colorId(image.getColorId())
                .path(image.getPath())
                .isMain(image.getIsMain())
                .build();
    }
}
