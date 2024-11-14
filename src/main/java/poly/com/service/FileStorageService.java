package poly.com.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import poly.com.configuration.FileStorageProperties;
import poly.com.dto.UploadedFileInfo;
import poly.com.exception.FileNotFoundException;
import poly.com.exception.FileStorageException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path fileCompanyImageStorageLocation;
    private final Path fileProfileImageStorageLocation;

    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileCompanyImageStorageLocation = Paths.get(fileStorageProperties.getUploadCompanyImageDir())
                .toAbsolutePath().normalize();

        this.fileProfileImageStorageLocation = Paths.get(fileStorageProperties.getUploadProfileImageDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(fileCompanyImageStorageLocation);
            Files.createDirectories(fileProfileImageStorageLocation);

        }catch (Exception ex){
            throw new FileStorageException("Cound not create the directory where "+
                    "the upload file will be stored",ex);
        }
    }

    public String storeImageCompanyFile(MultipartFile file) {
        return storeFile(fileCompanyImageStorageLocation, file);
    }

    private String storeFile(Path location, MultipartFile file) {
        UUID uuid = UUID.randomUUID();

        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String fileName = uuid.toString() + "." + ext;

        try {
            if (fileName.contains("..")){
               throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);

            }

            Path targetLocation = location.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        }catch (Exception ex){
            throw new FileStorageException("Cound not store file " + fileName + ".Please try again!",ex);
        }
    }

    public UploadedFileInfo storeUploadedImageCompanyFile(MultipartFile file) {
        return storeUpaloadedFile(fileCompanyImageStorageLocation, file);
    }

    private UploadedFileInfo storeUpaloadedFile(Path location, MultipartFile file) {
        UUID uuid = UUID.randomUUID();

        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String fileName = uuid.toString() + "." + ext;

        try {
            if (fileName.contains("..")){
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);

            }

            Path targetLocation = location.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            UploadedFileInfo info = new UploadedFileInfo();
            info.setFileName(fileName);
            info.setUid(uuid.toString());
            info.setName(StringUtils.getFilename(file.getOriginalFilename()));


            return info;
        }catch (Exception ex){
            throw new FileStorageException("Cound not store file " + fileName + ".Please try again!",ex);
        }
    }

    public Resource loadCompanyImageResource(String fileName) {
        return loadFileAsResource(fileCompanyImageStorageLocation, fileName);
    }

    private Resource loadFileAsResource(Path location, String fileName) {

        try {
            Path file = location.resolve(fileName).normalize();

            Resource resource = new UrlResource(file.toUri());

            if (resource.exists()){
                return resource;
            }else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (Exception  ex) {
            throw new FileNotFoundException("File not found " + fileName + ".Please try again!",ex);
        }
    }

    public void deleteCompanyImageFile(String fileName) {
        deleteFile(fileCompanyImageStorageLocation, fileName);
    }

    private void deleteFile(Path location, String fileName) {
        try {
            Path filePath = location.resolve(fileName).normalize();

            if (!Files.exists(filePath)){
                throw new FileNotFoundException("File not found " + fileName);
            }
            Files.delete(filePath);
        } catch (IOException ex) {
            throw new FileNotFoundException( "File not found " + fileName ,ex);
        }
    }

// Image Profile
    public String storeImageProfileFile(MultipartFile file) {
        return storeProFile(fileProfileImageStorageLocation, file);
    }

    private String storeProFile(Path location, MultipartFile file) {
        UUID uuid = UUID.randomUUID();

        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String fileName = uuid.toString() + "." + ext;

        try {
            if (fileName.contains("..")){
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);

            }

            Path targetLocation = location.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        }catch (Exception ex){
            throw new FileStorageException("Cound not store file " + fileName + ".Please try again!",ex);
        }
    }

    public UploadedFileInfo storeUploadedImageProfileFile(MultipartFile file) {
        return storeUpaloadedProFile(fileProfileImageStorageLocation, file);
    }

    private UploadedFileInfo storeUpaloadedProFile(Path location, MultipartFile file) {
        UUID uuid = UUID.randomUUID();

        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String fileName = uuid.toString() + "." + ext;

        try {
            if (fileName.contains("..")){
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);

            }

            Path targetLocation = location.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            UploadedFileInfo info = new UploadedFileInfo();
            info.setFileName(fileName);
            info.setUid(uuid.toString());
            info.setName(StringUtils.getFilename(file.getOriginalFilename()));


            return info;
        }catch (Exception ex){
            throw new FileStorageException("Cound not store file " + fileName + ".Please try again!",ex);
        }
    }

    public Resource loadProfileImageResource(String fileName) {
        return loadProFileAsResource(fileProfileImageStorageLocation, fileName);
    }

    private Resource loadProFileAsResource(Path location, String fileName) {

        try {
            Path file = location.resolve(fileName).normalize();

            Resource resource = new UrlResource(file.toUri());

            if (resource.exists()){
                return resource;
            }else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (Exception  ex) {
            throw new FileNotFoundException("File not found " + fileName + ".Please try again!",ex);
        }
    }

    public void deleteProfileImageFile(String fileName) {
        deleteProFile(fileProfileImageStorageLocation, fileName);
    }

    private void deleteProFile(Path location, String fileName) {
        try {
            Path filePath = location.resolve(fileName).normalize();

            if (!Files.exists(filePath)){
                throw new FileNotFoundException("File not found " + fileName);
            }
            Files.delete(filePath);
        } catch (IOException ex) {
            throw new FileNotFoundException( "File not found " + fileName ,ex);
        }
    }
}
