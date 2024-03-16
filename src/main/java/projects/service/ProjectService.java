package projects.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exceptions.DbException;

public class ProjectService {
	
	//read the SQL file & set a constant
		private static final String SCHEMA_FILE = "projects-schema.sql";
		private static final String DATA_FILE = "projects_data.sql";
		
		//instance variable for the ProjectsDao
		private ProjectDao projectDao = new ProjectDao();
		
		
		public Project fetchProjectById(Integer projectId) {
			return projectDao.fecthProjectById(projectId).orElseThrow(() -> new NoSuchElementException(
					"Project ID: " + projectId + " does not exist."));
		}
		
		//--------- METHOD: Create & Populate Tables -----------------------------
		
		public void createAndPopulateTables() {
			loadFromFile(SCHEMA_FILE);
			loadFromFile(DATA_FILE);
		}


		//--------- METHOD: Load From File ---------------------------------------
		
		private void loadFromFile(String fileName) {
			
			String content = readFileContent(fileName);
			
			List<String> sqlStatements = convertContentToSqlStatements(content);
			
			sqlStatements.forEach(line -> System.out.println(line));
			
			projectDao.executeBatch(sqlStatements);
			
		}

		
		//--------- METHOD: Convert to SQL Statement -----------------------------

		private List<String> convertContentToSqlStatements(String content) {
			
			//Converting each to sql statement to a string. Know they are statements because looking for semicolon.
			//then looking for whitespace and removing.
			//then extract the lines and put them in a list.
			content = removeComments(content);
			content = replaceWhitespaceSequenceWithSingleSpace(content);
			
			return extractLinesFromContent(content);
		}


		//--------- METHOD: Extract Lines ----------------------------------------

		private List<String> extractLinesFromContent(String content) {
			
			List <String> lines = new LinkedList<>();
			
			//while the string isn't empty we will set the semicolon to the index any time there is a ";".
			//this is for the last line
			while (!content.isEmpty()) {
				int semicolon = content.indexOf(";");
				
				if(semicolon == -1) {
					if(!content.isBlank()) {
						lines.add(content);
					}
					//content = empty causes us to exit loop
					content = "";
					
					//this is for all other lines
				} else {
					lines.add(content.substring(0, semicolon).trim());
					content = content.substring(semicolon + 1);
				}
			}
			return lines;
		}
		
		//--------- METHOD: Replace Whitespace ----------------------------------------
		
		private String replaceWhitespaceSequenceWithSingleSpace(String content) {
			
			//regular expression(first param), second para is what to replace with - see more about regular expressions on Regex Java Cheatsheet
			return content.replaceAll("\\s+", " ");
		}
		
		//--------- METHOD: Remove Comments -------------------------------------------
		
		private String removeComments(String content) {
			
			StringBuilder sb = new StringBuilder(content);
			
			//keeping position of comments
			int commentPos = 0;
			
			//looping through our content, going from comment to comment
			//first param is what we are looking for + starting position (if it finds the comment, returns the position otherwise return -1)
			//assignment is written in the loop
			while((commentPos = sb.indexOf("-- ", commentPos)) != -1) {
				
				//finding the end of the line
				int eolPos = sb.indexOf("\n", commentPos + 1);
				
				if (eolPos == -1) {
					sb.replace(commentPos, sb.length(), "");
				} else {
					sb.replace(commentPos, eolPos + 1, "");
				}
			}
			return sb.toString();
		}
		
		//--------- METHOD: Read File Content ----------------------------------------
		
		private String readFileContent(String fileName) {
			
			//.getClass returns the Project Service class
			//.getClassLoader will get resources - when you dn't pass in a path it will load from java class path (will load schema as single string).
			try {
				Path path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
				return Files.readString(path);
				
			} catch (Exception e){
				throw new DbException(e);
			}
			
		}
		
		
		public static void main(String[] args) {
			new ProjectService().createAndPopulateTables();
		}

		//--------- METHOD: Add Project ----------------------------------------

		public Project addProject(Project project) {
			
			return projectDao.insertProject(project);
		}

		//--------- METHOD: Fetch Projects ----------------------------------------

		public List<Project> fetchProjects() {
			
			return projectDao.fetchAllProjects();
		}

}
