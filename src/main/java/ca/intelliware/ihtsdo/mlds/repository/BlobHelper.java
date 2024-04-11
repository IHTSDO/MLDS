package ca.intelliware.ihtsdo.mlds.repository;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;

@Service
public class BlobHelper {
	@Resource
	EntityManager entityManager;

	public Blob createBlobFrom(MultipartFile multipartFile) throws IOException {
		InputStream inputStream = multipartFile.getInputStream();
		long size = multipartFile.getSize();
		return createBlob(inputStream, size);
	}

	public Blob createBlob(InputStream inputStream, long size) {
		Blob blob = null;
		Session session = (Session) entityManager.getDelegate();
		 blob = session.getLobHelper().createBlob(inputStream, size);
		return blob;
	}
}
