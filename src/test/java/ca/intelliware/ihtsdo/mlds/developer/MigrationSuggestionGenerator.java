package ca.intelliware.ihtsdo.mlds.developer;

import javax.annotation.Resource;
import javax.sql.DataSource;

import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.output.DiffOutputControl;
import liquibase.ext.hibernate.database.connection.HibernateConnection;
import liquibase.integration.commandline.CommandLineUtils;

import org.springframework.context.support.GenericXmlApplicationContext;

public class MigrationSuggestionGenerator {
	GenericXmlApplicationContext genericXmlApplicationContext;
	
	@Resource
	DataSource dataSource;

	private DiffOutputControl diffControl = new DiffOutputControl(false,false,false, null);
	
	public static void main(String[] args) throws Exception {
		new MigrationSuggestionGenerator().run();
	}

	private void run() throws Exception {
		initSpring();
		
		Database jdbcConnectionDatabase = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));

		String hibernateUrl = "hibernate:ejb3:mlds?hibernate.dialect=org.hibernate.dialect.PostgreSQL82Dialect";
		Database hibernateDatabase = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(new HibernateConnection(hibernateUrl)));

		CommandLineUtils.doDiffToChangeLog(null,hibernateDatabase, jdbcConnectionDatabase, diffControl, null);
	}

	private void initSpring() {
		genericXmlApplicationContext = new GenericXmlApplicationContext("applicationContext-integrationTest.xml", "applicationContext.xml");
		genericXmlApplicationContext.getAutowireCapableBeanFactory().autowireBean(this);
	}
}
