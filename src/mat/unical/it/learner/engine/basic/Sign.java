package mat.unical.it.learner.engine.basic;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public enum Sign {
    
    NEGATIVE, POSITIVE, UNDEFINED;
    

//    public static void main(String[] args) {
//	
//	Sign s = Sign.POSITIVE;
//	Sign s1 = Sign.POSITIVE;
//	Sign s2 = Sign.UNDEFINED;
//	
//	System.out.println(s.equals(s1));
//	System.out.println(s.equals(s2));
//	
//    }
    
    public static Iterator<Sign> getIteratorOnSigns() {
	
	List<Sign> l = new LinkedList<Sign>();
	l.add(Sign.POSITIVE);
	l.add(Sign.NEGATIVE);
	Iterator<Sign> it = l.iterator();
	return it;
    }
    
    public String toString() {

	if (this.equals(Sign.POSITIVE)) {
	    return "+";
	}
	else if (this.equals(Sign.NEGATIVE)) {
	    return "-";
	}
	else return "";
    }
}