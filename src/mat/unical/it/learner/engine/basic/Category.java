package mat.unical.it.learner.engine.basic;


/**
 * It represent a category with its training set. In thi representation, a
 * category consist of a name, a number identifier and a set of documents
 * ideally associated to it.
 */
public class Category implements java.lang.Cloneable, java.io.Serializable {

    /** The category name */
    private String categoryName;

    /** The category id */
    private int categoryNumber;
    
    private int noOfDocuments = 0;
    
    private String initialManualRules = null;
    
    private String optimizationRules = null;
    

    /**
         * Create a category by its name and its idNo
         * 
         * @param str
         * @param no
         */
    public Category(String name, int idNo) {
	categoryName = name;
	categoryNumber = idNo;
    }

    /**
         * Create a new, empty category
         */
    public Category() {
    }

    /**
         * @return Returns the categoryName.
         */
    public String getCategoryName() {

	return categoryName;
    }

    /**
         * @param categoryName The categoryName to set.
         */
    public void setCategoryName(String categoryName) {

	this.categoryName = categoryName;
    }

    /**
         * @return Returns the categoryNumber.
         */
    public int getCategoryNumber() {

	return categoryNumber;
    }

    /**
         * @param categoryNumber The categoryNumber to set.
         */
    public void setCategoryNumber(int categoryNumber) {

	this.categoryNumber = categoryNumber;
    }


    /**
         * @return the number of documents
         */
    public int getNoOfDocuments() {

	return noOfDocuments;
    }

    /**
         * Return a String representation of this category. Return the name of
         * this category
         */
    public String toString() {
	return categoryName;
    }

    public boolean equals(Category c) {
	return (this.categoryName.equals(c.categoryName) && c.categoryNumber == this.categoryNumber);
    }

    public void setNoOfDocuments(Integer categorySizeTrainingSetSize) {

	this.noOfDocuments = categorySizeTrainingSetSize;
    }

    public String getInitialManualRules() {
        return initialManualRules;
    }

    public void setInitialManualRules(String initialManualRules) {
        this.initialManualRules = initialManualRules;
    }

    public String getOptimizationRules() {
        return optimizationRules;
    }

    public void setOptimizationRules(String optimizationRules) {
        this.optimizationRules = optimizationRules;
    }

    
    
}
