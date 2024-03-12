package projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import projects.entity.Project;
import projects.exceptions.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {
	
	public static final String PROJECT_TABLE = "project";
	public static final String MATERIAL_TABLE = "material";
	public static final String STEP_TABLE = "step";
	public static final String CATEGORY_TABLE = "category";
	public static final String PROJECT_CATEGORY = "project_category";
	
	//--------- METHOD: Insert Project ----------------------------------------

		public Project insertProject(Project project) {
			
			// @formatter:off
			String sql = "" +  "INSERT INTO" + PROJECT_TABLE + " " 
							+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
							+ "VALUES "
							+ "(?, ?, ?, ?, ?)";
			
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
