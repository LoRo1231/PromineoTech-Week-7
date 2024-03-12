/**
 * 
 */
package projects.entity;

/**
 * @author Promineo
 *
 */
public class Category {
  private Integer categoryId;
  private String categoryName;
  
//----------------- GETTERS & SETTERS ---------------------- use source menu & getters & setters

  public Integer getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Integer categoryId) {
    this.categoryId = categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

//----------------- MEHTOD: Set to a String ---------------------
  
  @Override
  public String toString() {
    return "ID=" + categoryId + ", categoryName=" + categoryName;
  }
}
