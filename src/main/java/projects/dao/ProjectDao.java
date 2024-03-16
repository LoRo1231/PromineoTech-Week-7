package projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exceptions.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {
	
	public static final String PROJECT_TABLE = "project";
	public static final String MATERIAL_TABLE = "material";
	public static final String STEP_TABLE = "step";
	public static final String CATEGORY_TABLE = "category";
	public static final String PROJECT_CATEGORY_TABLE = "project_category";
	
	public Optional <Project> fecthProjectById(Integer projectId){
		
		String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		
		try(Connection conn = DbConnection.getConnection()){
			
			startTransaction(conn);
			
			try {
				Project project = null;
				
				try(PreparedStatement stmt = conn.prepareStatement(sql)){
					setParameter(stmt, 1, projectId, Integer.class);
					
					try(ResultSet rs = stmt.executeQuery()){
						if(rs.next()) {
							project = extract(rs, Project.class);
						}
					}
					
					if(Objects.nonNull(project)) {
						
						//All methods done in the same transaction and same connection
						project.getMaterials()
						.addAll(fetchMaterials(conn, projectId));
						
						project.getSteps()
						.addAll(fetchSteps(conn, projectId));
						
						project.getCategories()
						.addAll(fetchCategories(conn, projectId));
						
					}
					
					return Optional.ofNullable(project);
				}
				
			} catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}
	
	//--------- METHOD: Fetch Categories ----------------------------------------
	
	private List<Category> fetchCategories(Connection conn, Integer projectId) throws SQLException{
		
		// @formatter:off
		String sql = ""
				+ "SELECT c.* "
				+ "FROM " + PROJECT_CATEGORY_TABLE + " pc "
				+ "JOIN " + CATEGORY_TABLE + " c USING (category_id) "
				+ "WHERE project_id = ? "
				+ "ORDER BY c.category_name";
		// @formatter:on
		
		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter(stmt, 1, projectId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery()){
				List<Category> categories = new LinkedList<Category>();
				
				while(rs.next()) {
					categories.add(extract(rs, Category.class));
				}
				
				return categories;
			}
		}
	}
	
	//--------- METHOD: Fetch Steps ----------------------------------------

	private List<Step> fetchSteps(Connection conn, Integer projectId) throws SQLException{
		
		// @formatter:off
		String sql = "SELECT * FROM " + STEP_TABLE + " s WHERE s.project_id = ?";
		// @formatter:on
		
		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter(stmt, 1, projectId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery()){
				List<Step> steps = new LinkedList<Step>();
				
				Step step = extract(rs, Step.class);
				
				steps.add(step);
				
				return steps;
			}
		}
	}
	
	//--------- METHOD: Fetch Materials ----------------------------------------

	private List<Material> fetchMaterials(Connection conn, Integer projectId) throws SQLException {
		
		// @formatter:off
		String sql = "SELECT * FROM " + MATERIAL_TABLE + " m WHERE m.project_id = ? "
					+ "ORDER BY m.material_order";
		// @formatter:on
		
		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter(stmt, 1, projectId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery()){
				List<Material> materials = new LinkedList<Material>();
				
				Material material = extract(rs, Material.class);
				
				materials.add(material);
				
				return materials;
			}
		}
		
		
	}

	//--------- METHOD: Fetch All Projects ----------------------------------------

		public List<Project> fetchAllProjects() {
		
			String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";
			
			
			try(Connection conn = DbConnection.getConnection()){
				
				startTransaction(conn);
				
				try(PreparedStatement stmt = conn.prepareStatement(sql)){
					try(ResultSet rs = stmt.executeQuery()){
						List<Project> projects = new LinkedList<>();
						
						while (rs.next()) {
							projects.add(extract(rs, Project.class));
						}
						return projects;
					}
				} catch (Exception e) {
					rollbackTransaction(conn);
					throw new DbException(e);
				}
				
			} catch (SQLException e) {
		
				throw new DbException(e);
			}
		}
	
	//--------- METHOD: Insert Project ----------------------------------------

		public Project insertProject(Project project) {
			
			// @formatter:off
			String sql = "" + "INSERT INTO " + PROJECT_TABLE + " " 
							+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
							+ "VALUES "
							+ "(?, ?, ?, ?, ?)";
			// @formatter:on
			
			try(Connection conn = DbConnection.getConnection()){
				startTransaction(conn);
				
				try(PreparedStatement stmt = conn.prepareStatement(sql)){
					setParameter(stmt, 1, project.getProjectName(), String.class);
					setParameter(stmt, 2, project.getEstimatedHours(), Integer.class);
					setParameter(stmt, 3, project.getActualHours(), Integer.class);
					setParameter(stmt, 4, project.getDifficulty(), Integer.class);
					setParameter(stmt, 5, project.getNotes(), String.class);
					
					stmt.executeUpdate();
					Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
					
					commitTransaction(conn);
					
					project.setProjectId(projectId);
					return project;
				} catch (Exception e) {
					rollbackTransaction(conn);
					throw new DbException(e);
				}
			} catch (SQLException e) {
				throw new DbException(e);		
				}
		}
		// formatter:on
		

		
	public void executeBatch (List <String> sqlBatch) {
			
			try(Connection conn = DbConnection.getConnection()){
				
				startTransaction(conn); //Method within DaoBase
				
				try(Statement stmt = conn.createStatement()){
					
					for(String sql : sqlBatch) { //adding each sql statement as a batch to the sql statement
						stmt.addBatch(sql);
					}
					
					stmt.executeBatch();
					commitTransaction(conn); //Method within DaoBase
					
				} catch (Exception e) {
					rollbackTransaction(conn);
					throw new DbException(e);
				}
			} catch (SQLException e) {
				throw new DbException(e);
			}
		}


}
