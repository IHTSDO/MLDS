package ca.intelliware.ihtsdo.mlds.web;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TemplateCollector {

	private ServletContext context;

	@Autowired
	public TemplateCollector(ServletContext context) throws IOException, URISyntaxException {
		this.context = context;
		getRelativeFiles("/js/app/templates");
	}
	
	public List<String> getRelativeFiles(String rootDir) throws IOException, URISyntaxException {
		final List<String> result = new ArrayList<String>();
		
		final Path templateRoot = Paths.get(context.getRealPath(rootDir));
		Files.walkFileTree(templateRoot, new SimpleFileVisitor<Path>(){
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Path relativePath = templateRoot.relativize(file);
				result.add(relativePath.toString());
		        return FileVisitResult.CONTINUE;
			}
		});
		
		return result;
	}
}
