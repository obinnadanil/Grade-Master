package obi.com.grademaster.service;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.io.IOException;

@Component
public class ResourceLoaderService {

    private final ResourceLoader resourceLoader;

    public ResourceLoaderService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Image loadImage() throws IOException {
        String imagePath = "classpath:static/images/schoolLogo.jpg";
        Resource resource = resourceLoader.getResource(imagePath);

        try (InputStream inputStream = resource.getInputStream()) {
            byte[] imageData = inputStream.readAllBytes();
            return new Image(ImageDataFactory.create(imageData));
        }
    }
}

