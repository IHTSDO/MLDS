
package ca.intelliware.ihtsdo.mlds.repository;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;

@Service
public class BlobHelper {

    @Resource
    EntityManager entityManager;

    public Blob createBlobFrom(MultipartFile multipartFile) throws IOException {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            return createBlob(inputStream, multipartFile.getSize());
        }
    }

    public Blob createBlob(InputStream inputStream, long size) {
        Session session = entityManager.unwrap(Session.class);

        return session.doReturningWork(connection -> {
            Blob blob = connection.createBlob();

            try (OutputStream outputStream = blob.setBinaryStream(1)) {
                byte[] buffer = new byte[8192];
                long remaining = size;

                while (remaining > 0) {
                    int bytesRead = inputStream.read(
                        buffer, 0, (int) Math.min(buffer.length, remaining)
                    );

                    if (bytesRead == -1) {
                        throw new IOException("Input stream ended before the expected size");
                    }

                    outputStream.write(buffer, 0, bytesRead);
                    remaining -= bytesRead;
                }
            } catch (IOException e) {
                blob.free();
                throw new java.io.UncheckedIOException(e);
            }

            return blob;
        });
    }
}
