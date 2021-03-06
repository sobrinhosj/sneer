
package spikes.gandhi.memory.thirdparty;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;

// ----------------------------------------------------------------------------
/**
 * Abstract base class for all node implementations in this package. 
 * 
 * @author (C) <a href="http://www.javaworld.com/columns/jw-qna-index.shtml">Vlad Roubtsov</a>, 2003
 */
abstract class AbstractProfileNode implements IObjectProfileNode, Comparable<Object>
{
    // public: ................................................................
    
    // IObjectProfileNode:
    
    @Override
	public final int size ()
    {
        return m_size;
    }

    @Override
	public final IObjectProfileNode parent ()
    {
        return m_parent;
    }
    
    @Override
	@SuppressWarnings("rawtypes")
	public final IObjectProfileNode [] path ()
    {
        IObjectProfileNode [] path = m_path;
        if (path != null)
            return path;
		final LinkedList <ObjectProfileNode> _path = new LinkedList ();
		for (IObjectProfileNode node = this; node != null; node = node.parent ())
		    _path.addFirst ((ObjectProfileNode) node);
		
		path = new IObjectProfileNode [_path.size ()];
		_path.toArray (path);
		
		m_path = path; 
		return path;
    }
    
    @Override
	public final IObjectProfileNode root ()
    {
        IObjectProfileNode node = this;
        for (IObjectProfileNode parent = parent (); parent != null;
             node = parent, parent = parent.parent ());
        
        return node;
    }
    
    @Override
	public final int pathlength ()
    {
        final IObjectProfileNode [] path = m_path;
        if (path != null)
            return path.length;              
        int result = 0;
        for (IObjectProfileNode node = this; node != null; node = node.parent ())
			++ result;
                
        return result;

    }
    
    @Override
	public final String dump ()
    {
        final StringWriter sw = new StringWriter ();
        final PrintWriter out = new PrintWriter (sw);
        
        final INodeVisitor visitor =
            ObjectProfileVisitors.newDefaultNodePrinter (out, null, null, ObjectProfiler.SHORT_TYPE_NAMES);
        traverse (null, visitor);
        
        out.flush ();
        return sw.toString ();
    }
    
    // Comparable:
    
    @Override
	public final int compareTo (final Object obj)
    {
        return ((AbstractProfileNode) obj).m_size - m_size;
    }
    
    @Override
	public String toString ()
    {
        return super.toString () + ": name = " + name () + ", size = " + size ();
    }
    
    // protected: .............................................................
    
    // package: ...............................................................
    
    
    AbstractProfileNode (final IObjectProfileNode parent)
    {
        m_parent = parent;
    }
    

    int m_size;
    
    static final IObjectProfileNode [] EMPTY_OBJECTPROFILENODE_ARRAY = new IObjectProfileNode [0];
    
    // private: ...............................................................
    
    
    private final IObjectProfileNode m_parent;
    private transient IObjectProfileNode [] m_path;

} // end of class
// ----------------------------------------------------------------------------