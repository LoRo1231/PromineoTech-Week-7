package projects;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.dao.DbConnection;
import projects.entity.Project;
import projects.exceptions.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	
	private Scanner scanner = new Scanner(System.in);
	//allows user input through the console
	
	private ProjectService projectService = new ProjectService();
	
	// @formatter:off
	private List<String> operations = List.of(
			"1) Create and populate all tables.",
			"2) Add a project.",
			"3) List projects.",
			"4) Select a project."
			);
	private Object currentProject;
	// @formatter:on
			
			
	
	//--------------------------------------------------------------------

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//DbConnection.getConnection();
		
		new ProjectsApp().displayMenu();

	}
	
	//------------- METHOD: Display Menu ----------------------------------

	private void displayMenu() {
		
		boolean done = false;
		
		while(!done) {
			
			int operation = getOperation();
			
			try {
				
				//switch allows us to handle the -1 return form operation
				switch(operation) {
				case -1:
					done = exitMenu();
					break;
				
				//if returned 1 then we want to create the tables
				case 1:
					createTables();
					break;
					
				case 2:
					addProject();
					break;
					
				case 3:
					listProjects();
					break;
					
				case 4:
					setCurrentProject();
					break;
					
				default:
					System.out.println("\n" + operation + " is not vaild, try again.");
					break;
				}
			} catch (Exception e){
				System.out.println("\nError: " + e.toString() + "Try again.");
			}
		}
		
	}
	
	//------------- METHOD: Select Project -------------------------------------------
	
	private void setCurrentProject() {
		
		List<Project> projects = listProjects();
		
		Integer projectId = getIntInput("Enter project ID");
		
		currentProject = null;
		
		for(Project project : projects) {
			if(project.getProjectId().equals(projectId)) {
				currentProject = projectService.fetchProjectById(projectId);
				break;
			}
		}
		
		if(Objects.isNull(currentProject)) {
			System.out.println("\nInvaild project selected.");
		}
	}

	//------------- METHOD: List Projects -------------------------------------------
	
	private List<Project> listProjects() {
		
		List<Project> projects = projectService.fetchProjects();
		
		System.out.println("\nProjects:");
		
		projects.forEach(project -> System.out.println("   " + project.getProjectId() + ": " + project.getProjectName()));
		
		return projects;
	}

	//------------- METHOD: Add Project -------------------------------------------

	private void addProject() {
		String name = getStringInput("Enter project name.");
		Integer estimatedHours = getIntInput("Enter estiamted hours for completion.");
		Integer actualHours = getIntInput("Enter the actual hours for completion.");
		Integer difficulty = getIntInput("Enter difficulty level (Choose 1 - 10).");
		String notes = getStringInput("Enter project notes.");
		
		LocalTime estimatedTime = hoursToLocalTime(estimatedHours);
		LocalTime actualTime = hoursToLocalTime(actualHours);
		
		Project project = new Project();
		
		project.setProjectName(name);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You added this project:\n" + dbProject);
		
		currentProject = projectService.fetchProjectById(dbProject.getProjectId());
		
	}
	
	//------------- METHOD: Hours to Local Time ----------------------------------

	private LocalTime hoursToLocalTime(Integer numMinutes) {
		
		int min = Objects.isNull(numMinutes) ? 0 : numMinutes;
		int hours = min / 60;
		int minutes = min % 60;
		
		return LocalTime.of(hours, minutes);
	}

	//------------- METHOD: Create Tables ------------------------------------------
	private void createTables() {
		
		projectService.createAndPopulateTables();
		System.out.println("\nTables created and populated");
		
	}
	
	//------------- METHOD: Exit Menu ----------------------------------
	
	private boolean exitMenu() {
		
		System.out.println("\nExiting Menu.");
		return true;
	}
	
	//------------- METHOD: Get Operation ----------------------------------
	
	private int getOperation() {
		
		printOperations();
		
		Integer op = getIntInput("\nEnter an operation number (press enter to exit).");
		
		//if null, return -1 otherwise return converted int
		return Objects.isNull(op) ? -1 : op;
	}


	//------------- METHOD: Print Operation ----------------------------------
	
	private void printOperations() {
		
		System.out.println();
		System.out.println("Here's what you can do:");
		
		operations.forEach(op -> System.out.println("   " + op));
	}
	
	//------------- METHOD: Get Int Input ----------------------------------
	
	private Integer getIntInput(String prompt) {
		
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a vaild number.");
		}
	}
	
	//------------- METHOD: Get Double Input ----------------------------------
	
		private Double getDoubleInput(String prompt) {
			
			String input = getStringInput(prompt);
			
			if(Objects.isNull(input)) {
				return null;
			}
			try {
				return Double.parseDouble(input);
			} catch (NumberFormatException e) {
				throw new DbException(input + " is not a vaild number.");
			}
		}
	
	//------------- METHOD: Get String Input ----------------------------------

	private String getStringInput(String prompt) {
		
		System.out.println(prompt + ": ");
		String line = scanner.nextLine();
		
		return line.isBlank() ? null : line.trim();
	}
	
	

}
