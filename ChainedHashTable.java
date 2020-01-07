import java.util.Arrays;

public class ChainedHashTable implements HashTable{
	private int numKeys;
	private EntryNode[] table; 
	
	private class EntryNode{
		private Object key; 
		private LLQueue<Object> values; 
		private EntryNode next;
		private EntryNode prev;
		
		private EntryNode(Object key, Object value) {
			this.key = key; ; 
			values = new LLQueue<Object>(); 
			values.insert(value);
			next = null; 
			prev = null; 
			
		}
	}
	/*
	 * constructor for chainedHashTable
	 */
	public ChainedHashTable(int size) {
		if (size <= 0) {
            throw new IllegalArgumentException("size must be positive");
        }
		table = new EntryNode[size];
		numKeys = 0; 
	}
	
    /* first hash function */
    public int h1(Object key) {
        int h1 = key.hashCode() % table.length;
        if (h1 < 0) {
            h1 += table.length;
        }
        return h1;
    }
    /*
     * insert - insert the specified (key, value) pair in the hash table.
     * Returns true if the pair can be added and false if there is overflow.
     */
    public boolean insert (Object key, Object value) {
    	int i = h1(key);
    	//EntryNode head = table[i]; //first node
    	EntryNode trav = table[i]; //traversal 
    	
    	if(table[i] == null) {
    		EntryNode inserted = new EntryNode(key, value);
    		table[i] = inserted;
    		numKeys++;
    	}
    	else {
    		while(trav != null) {
    			if(trav.key.equals(key)) {
    				trav.values.insert(value);
    				return true; 
    			}
    			trav = trav.next; //traverses
    		}
    		//if we get here then the key is not in the bucket
    		EntryNode inserted = new EntryNode(key, value);
    		inserted.next = table[i]; 
    		table[i].prev = inserted; 
    		table[i] = inserted; 
    		numKeys++;
    	}
    	return true; 
    }
    
    /*
     * search - search for the specified key and return the
     * associated collection of values, or null if the key 
     * is not in the table
     */
    public Queue<Object> search(Object key){
    	if (key == null) {
            throw new IllegalArgumentException("key must be non-null");
        }
    	
    	int i = h1(key); 
    	
    	if(table[i] == null || table[i].key == null) {
    		return null; 
    	}
    	else if(table[i] != null) {
    		while(table[i] != null) {
    			if(table[i].key.equals(key)) {
    				return table[i].values; 
    			}
    			table[i] = table[i].next; //traverses
    		}
    	}
    	return null; //if we get here then the key is not in the table
    }
    
    /* 
     * remove - remove from the table the entry for the specified key
     * and return the associated collection of values, or null if the key 
     * is not in the table
     */
    public Queue<Object> remove(Object key){
    	if (key == null) {
            throw new IllegalArgumentException("key must be non-null");
        }
    	
    	int i = h1(key); 
    	if(table[i] == null) {
    		return null; 
    	}
    	else if(table[i].key.equals(key)) {//node to remove is the head
    		LLQueue<Object> save = table[i].values;
    		if(table[i].next == null) {//only node
    			table[i] = null;
    		}
    		else {//not only node
    			table[i] = table[i].next; 
    			table[i].prev.next = null; 
    			table[i].prev = null;
    		}
    		return save; 
    	}
    	else { //node to remove is not the head
    		LLQueue<Object> save = null; 
    		EntryNode trav = table[i]; //traversal node
    		while( trav != null) {
    			if(trav.key.equals(key)) {
    				save = trav.values; 
    				if(trav.next == null) { //last node
    					trav.prev.next = null; 
    					trav.prev = null; 
    				}
    				else { //inbetween 2 nodes
    					trav.prev.next = trav.next; 
    					trav.next.prev = trav.prev; 
    					trav.next = null; 
    					trav.prev = null; 
    				}
    			}
    			trav = trav.next; 
    		}
    		return save; //null if key wasn't in the queue
    	}
    }
    	
    
    /*
     * each position in the table is represented by the key(s) in that
     * or the word null if there are no keys in that position
     */
    public String toString() {
    	String start = "{";
    	String end = "}"; 
    	String middle = "";
    	
    	for(int i = 0; i < table.length; i++) {
    		if(table[i] == null) {
    			middle += "null";
    			if(i < table.length - 1) {
    				middle += ", ";
    			}
    		}
    		else {
    			if(table[i].next == null) {
    				middle += "[";
    				middle += table[i].key;
    				middle += "]";
    	    			if(i < table.length - 1) {
    	    				middle += ", ";
    	    			}
    			}
    			else {
        			EntryNode trav = table[i];
        			middle += "[";
    				while(trav != null) {
    					middle += trav.key; 
    					if(trav.next != null) {
    						middle += ", ";
    					}
    					trav = trav.next;
    				}
    				middle += "]";
    				if(i < table.length - 1) {
	    				middle += ", ";
	    			}
    			}
    			
    		}
    	}
    	
    	return(start + middle + end); 
    }
    
    /*
     * accessor method for the number of keys
     */
    public int getNumKeys() {
    	return numKeys;
    }
    
    /*
     * load- returns number of keys/size of the table
     */
    
    public double load() {
    	double load = (double)numKeys/table.length;
    	return load; 
    }
    
    /*
     * returns array containing all the keys in the hashtable
     */
    public Object[] getAllKeys() {
    	Object[] keys = new Object[numKeys];
    	int index = 0; //index for keys array
    	for(int i = 0; i < table.length; i++) {
    		if(table[i] != null) {
    			while(table[i] != null) {
    				keys[index] = table[i].key; 
    				index++; 
    				table[i] = table[i].next;
    			}
    		}
    	}
    	return keys; 
    }
    
    /*
     * grows table to a new, given, size!
     */
    public void resize(int newSize) {

    	if(newSize < table.length) {
    		throw new IllegalArgumentException();
    	}
    	else if(newSize == table.length) {
    		return; 
    	}
    	else {
    		EntryNode[] oldTable = table;
    		table = new EntryNode[newSize];
    		for(int i = 0; i < oldTable.length; i++) {
    			//System.out.println(oldTable[i].key);
    			if(oldTable[i] != null) {
    				//System.out.println(oldTable[i].key);
    				while(oldTable[i] != null) {
    					//System.out.println(oldTable[i].key);
    					insert(oldTable[i].key, oldTable[i].values);
    					oldTable[i] = oldTable[i].next;
    				}
    			}
    		}
    	}
     
    }

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        System.out.println("--- testing insert() ---");
        System.out.println();
        System.out.println("(1) inserting elements");
        try {
        	ChainedHashTable table = new ChainedHashTable(5);
        	table.insert("howdy", 15);
        	table.insert("goodbye", 15);
        	table.insert("apple", 15);
        	String results = table.toString();
            String expected = "{[apple, howdy], null, null, [goodbye], null}";
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.contentEquals(expected));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        }
                           
        System.out.println();  
        
        System.out.println("--- testing insert() ---");
        System.out.println();
        System.out.println("(2) inserting elements");
        try {
        	ChainedHashTable table = new ChainedHashTable(5);
        	table.insert(1, "howdy");
        	table.insert(4, "goodbye");
        	table.insert(17, "apply");
        	String results = table.toString();
            String expected = "{null, [1], [17], null, [4]}";
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.contentEquals(expected));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        }
                           
        System.out.println();  
        
        System.out.println("--- testing search() ---");
        System.out.println();
        System.out.println("(3) searching for key in middle of bucket");
        try {
        	ChainedHashTable table = new ChainedHashTable(5);
        	table.insert(1, "howdy");
        	table.insert(4, "goodbye");
        	table.insert(17, "apply");
        	table.insert(6, "this");
        	table.insert(11, "is");
        	table.insert(16, "test");
        	Object results = table.search(6);
            Object expected = "{this}";
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.equals(expected));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        }
                           
        System.out.println(); 
        
        System.out.println("--- testing search() ---");
        System.out.println();
        System.out.println("(4) searching for key not in bucket");
        try {
        	ChainedHashTable table = new ChainedHashTable(5);
        	table.insert(1, "howdy");
        	table.insert(4, "goodbye");
        	table.insert(17, "apply");
        	table.insert(6, "this");
        	table.insert(11, "is");
        	table.insert(16, "test");
        	Object results = table.search("howdy");
            Object expected = null;
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.equals(expected));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        }
                           
        System.out.println(); 
        
        System.out.println("--- testing remove() ---");
        System.out.println();
        System.out.println("(5) removing key in middle of bucket");
        try {
        	ChainedHashTable table = new ChainedHashTable(5);
        	table.insert(1, "howdy");
        	table.insert(4, "goodbye");
        	table.insert(17, "apply");
        	table.insert(6, "this");
        	table.insert(11, "is");
        	table.insert(16, "test");
        	Object results = table.remove(6);
            Object expected = "{this}";
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.equals(expected));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        }
                           
        System.out.println(); 
        
        System.out.println("--- testing remove() ---");
        System.out.println();
        System.out.println("(6) removing key not in bucket");
        try {
        	ChainedHashTable table = new ChainedHashTable(5);
        	table.insert(1, "howdy");
        	table.insert(4, "goodbye");
        	table.insert(17, "apply");
        	table.insert(6, "this");
        	table.insert(11, "is");
        	table.insert(16, "test");
        	Object results = table.remove("howdy");
            Object expected = null;
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.equals(expected));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        }
                           
        System.out.println(); 
        
        System.out.println("--- testing toString() ---");
        System.out.println();
        System.out.println("(7) toString test");
        try {
        	ChainedHashTable table = new ChainedHashTable(5);
        	table.insert(1, "howdy");
        	table.insert(4, "goodbye");
        	table.insert(17, "apply");
        	String results = table.toString();
            String expected = "{null, [1], [17], null, [4]}";
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.contentEquals(expected));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        }
                           
        System.out.println();  
        
        System.out.println("--- testing toString() ---");
        System.out.println();
        System.out.println("(8) toString test");
        try {
        	ChainedHashTable table = new ChainedHashTable(5);
        	table.insert(1, "howdy");
        	table.insert(4, "goodbye");
        	table.insert(9, "yeehaw");
        	table.insert(17, "apply");
        	String results = table.toString();
            String expected = "{null, [1], [17], null, [9, 4]}";
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.contentEquals(expected));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        }
                           
        System.out.println(); 
        
        System.out.println("--- testing getNumKeys() ---");
        System.out.println();
        System.out.println("(9)");
        try {
        	ChainedHashTable table = new ChainedHashTable(5);
        	table.insert(1, "howdy");
        	table.insert(4, "goodbye");
        	table.insert(17, "apply");
        	table.insert(6, "this");
        	table.insert(11, "is");
        	table.insert(16, "test");
        	Object results = table.getNumKeys();
            Object expected = 6;
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.equals(expected));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        }
                           
        System.out.println(); 
        
        System.out.println("--- testing getNumKeys() ---");
        System.out.println();
        System.out.println("(10)");
        try {
        	ChainedHashTable table = new ChainedHashTable(5);
        	table.insert(1, "howdy");
        	table.insert(4, "goodbye");
        	table.insert(17, "apply");
        	table.insert(6, "this");
        	table.insert(11, "is");
        	table.insert(16, "test");
        	table.insert(16, "replicate");
        	Object results = table.getNumKeys();
            Object expected = 6;
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.equals(expected));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        }
                           
        System.out.println(); 
        
        System.out.println("--- testing load() ---");
        System.out.println();
        System.out.println("(11)");
        try {
        	ChainedHashTable table = new ChainedHashTable(5);
        	table.insert(1, "howdy");
        	table.insert(4, "goodbye");
        	table.insert(17, "apply");
        	table.insert(6, "this");
        	table.insert(11, "is");
        	table.insert(16, "test");
        	table.insert(16, "replicate");
        	Object results = table.load();
            Object expected = 1.2;
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.equals(expected));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        }
                           
        System.out.println(); 
        
        System.out.println("--- testing load() ---");
        System.out.println();
        System.out.println("(12)");
        try {
        	ChainedHashTable table = new ChainedHashTable(5);
        	table.insert(1, "howdy");
        	table.insert(4, "goodbye");
        	Object results = table.load();
            Object expected = 0.4;
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.equals(expected));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        }
                           
        System.out.println(); 
        
        System.out.println("--- testing getAllKeys() ---");
        System.out.println();
        System.out.println("(13)");
        try {
        	ChainedHashTable table = new ChainedHashTable(5);
        	table.insert("howdy", "howdy");
        	table.insert("goodbye", "goodbye");
        	table.insert("apple", 5);
        	table.insert("howdy", 25); 
        	Object results = Arrays.toString(table.getAllKeys());
            Object expected = "[ apple, howdy, goodbye ]";
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.equals(expected));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        }
                           
        System.out.println(); 
        
        System.out.println("--- testing getAllKeys() ---");
        System.out.println();
        System.out.println("(14)");
        try {
        	ChainedHashTable table = new ChainedHashTable(5);
        	table.insert("howdy", 1);
        	table.insert("goodbye", 5);
        	table.insert("apple", 5);
        	Object results = Arrays.toString(table.getAllKeys());
            Object expected = "[ apple, howdy, goodbye ]";
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.equals(expected));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        }
                           
        System.out.println(); 
        
        System.out.println("--- testing resize() ---");
        System.out.println();
        System.out.println("(15) resize from 5 to 7 ");
        try {
        	ChainedHashTable table = new ChainedHashTable(5);
        	table.insert("howdy", 15);
        	table.insert("goodbye", 15);
        	table.insert("apple", 15);
        	table.resize(7);
        	String results = table.toString();
            String expected = "{null, [apple], null, null, null, [howdy], [goodbye]}";
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.contentEquals(expected));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        }
                           
        System.out.println(); 
        
        System.out.println("--- testing resize() ---");
        System.out.println();
        System.out.println("(16) resize from 5 to 5 ");
        try {
        	ChainedHashTable table = new ChainedHashTable(5);
        	table.insert("howdy", 15);
        	table.insert("goodbye", 15);
        	table.insert("apple", 15);
        	table.resize(5);
        	String results = table.toString();
            String expected = "{[apple, howdy], null, null, [goodbye], null}";
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.contentEquals(expected));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        }
                           
        System.out.println(); 
	}
	
	

}
