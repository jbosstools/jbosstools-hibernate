/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.internal.core.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.eclipse.jdt.core.ICompilationUnit;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.exception.NestableRuntimeException;
import org.jboss.tools.hibernate.core.hibernate.Type;

class JavaCodeTokenizer extends Tokenizer {
	  public JavaCodeTokenizer(Reader input) {
	    super(input);
	  }

	  private int offset = 0, bufferIndex = 0, dataLen = 0;
	  private char putBackChar=0;
	  private boolean isLastDigit;
	  private static final int MAX_WORD_LEN = 255;
	  private static final int IO_BUFFER_SIZE = 1024;
	  private final char[] buffer = new char[MAX_WORD_LEN];
	  private final char[] ioBuffer = new char[IO_BUFFER_SIZE];

	  /** Returns true iff a character should be included in a token.  This
	   * tokenizer generates as tokens adjacent sequences of characters which
	   * satisfy this predicate.  Characters for which this is false are used to
	   * define token boundaries and are not included in tokens. */
	  protected boolean isTokenChar(char c){
		  return '_'!=c && Character.isJavaIdentifierPart(c);
	  }

	  /** Returns the next token in the stream, or null at EOS. */
	  public final Token next() throws java.io.IOException {
	    int length = 0;
	    int start = offset;
	    while (true) {
		
		  if(putBackChar!=0){
			  buffer[0] = putBackChar;
			  length=1;
			  start = offset - 1;
			  putBackChar=0;
		  }
	      final char c;

	      offset++;
	      if (bufferIndex >= dataLen) {
	        dataLen = input.read(ioBuffer);
	        bufferIndex = 0;
	      }
	      if (dataLen == -1) {
	        if (length > 0)
	          break;
	        else
	          return null;
	      } else
	        c = ioBuffer[bufferIndex++];

	      if (isTokenChar(c)) {               // if it's a token char
	        if (length == 0)			           // start of token
	          start = offset - 1;
	        
	        if(isLastDigit && !Character.isDigit(c)){ //end of digit token
	        	isLastDigit=false;
				putBackChar=c;
				break;
	        }
	        
			if(!isLastDigit && Character.isDigit(c)){ //start if new digit token
				putBackChar=c;
				isLastDigit=true;
				break;
			}
	        
			if(Character.isUpperCase(c) && length>0 && !Character.isUpperCase(buffer[length-1])){ //start of new token
				putBackChar=c;
				break;
			}
			
			buffer[length++] = c; // buffer char
			

	        if (length == MAX_WORD_LEN)		   // buffer overflow!
	          break;

	      } else if (length > 0)             // at non-Letter w/ chars
	        break;                           // return 'em

	    }

	    return new Token(new String(buffer, 0, length), start, start + length);
	  }
	}

final class JavaStopFilter extends TokenFilter {
	  private Set stopWords;

	  public static final String[] JAVA_STOP_WORDS = {
	    "java", "lang", "util", "sql", "io"
	  };

	  /** Builds an analyzer which removes words in JAVA_STOP_WORDS. */
	  public JavaStopFilter(TokenStream input) {
		  super(input);
		  stopWords = makeStopSet(JAVA_STOP_WORDS);
	  }

	  /** Builds an analyzer which removes words in the provided array. */
	  public JavaStopFilter(TokenStream input, String[] stopWords) {
        super(input);
	    this.stopWords = makeStopSet(stopWords);
	  }
	  public static final Set makeStopSet(String[] stopWords) {
		    HashSet<String> stopTable = new HashSet<String>(stopWords.length);
		    for (int i = 0; i < stopWords.length; i++)
		      stopTable.add(stopWords[i]);
		    return stopTable;
		  }

	  /**
	   * Returns the next input Token whose termText() is not a stop word.
	   */
	  public final Token next() throws IOException {
	    // return the first non-stop word found
	    for (Token token = input.next(); token != null; token = input.next())
	      if (!stopWords.contains(token.termText()))
	        return token;
	    return null;
	  }
	}

class JavaCodeAnalyzer extends Analyzer{
	
    double fuzzinessLevel;
    
    public TokenStream tokenStream(String fieldName, final Reader reader){
	    TokenStream result = new JavaCodeTokenizer(reader);
	    result = new LowerCaseFilter(result);
		result = new JavaStopFilter(result);
		return result;
	}
	public void displayTokens(String text) throws IOException{
		TokenStream ts=tokenStream("con",new StringReader(text));
		while(true){
			Token token=ts.next();
			if(token == null) break;
			System.out.println("["+token.termText() + ":" + token.type()+"]");
		}
	}
	public String createQuery(String text, boolean isFuzzy) throws IOException{
		TokenStream ts=tokenStream("con",new StringReader(text));
		StringBuffer buf=new StringBuffer(text.length());
		while(true){
			Token token=ts.next();
			if(token == null) break;
			buf.append(token.termText());
			if(isFuzzy) 
                buf.append("~"+fuzzinessLevel);
			buf.append(' ');
		}
		return buf.toString();
	}
	//added by Nick 18.04.2005
	public String createStemmingQuery(String text) throws IOException
	{
		TokenStream ts=new PorterStemFilter(tokenStream("con",new StringReader(text)));
		StringBuffer buf=new StringBuffer(text.length());
		while(true){
			Token token=ts.next();
			if(token == null) break;
			buf.append(token.termText());
			buf.append(' ');
		}
		return buf.toString();
	}
	//by Nick
}

public class SearchUtils {

    public static void setFuzzinessLevel(double level)
    {
        if (0 <= level && level <= 1)
        {
            analyzer.fuzzinessLevel = level;
        }
    }
    
    // added by Nick 06.07.2005
    //added for optimization purposes
    //to store stemmed names during queries
    public interface IClassSearchInfoHolder
    {
        public String getFullyQualifiedName();
        public String getShortName();
    }
    // by Nick


//  added by Nick 06.07.2005
    private static final JavaCodeAnalyzer analyzer = new JavaCodeAnalyzer();
    public static String getStemmedString(String source)
    {
        try {
            return analyzer.createStemmingQuery(source);
        } catch (IOException e) {
            ExceptionHandler.logThrowableError(e,e.getMessage());
        }
        return source;
    }
//  by Nick

    //added by Nick 27.05.2005
    private static int findIndex(String searchFor, String[] strings)
    {
        
        if (searchFor == null || strings == null)
            return -1;

        try{
            Directory directory = new RAMDirectory();  
            StandardAnalyzer stdAnalyzer=new StandardAnalyzer();
            IndexWriter writer = new IndexWriter(directory, analyzer, true);
            for (int j = 0; j < strings.length; j++) {
                  if(strings[j]==null) continue;
                  Document d = new Document();
                  d.add(Field.Text("name",strings[j]));
                  d.add(Field.Keyword("index",String.valueOf(j)));
                  writer.addDocument(d);
                  //analyzer.displayTokens(cols[j].getName());
                }
                writer.close();

                Searcher searcher = new IndexSearcher(directory);
                // changed by Nick 22.07.2005
                Query query = QueryParser.parse(searchFor,"name", stdAnalyzer);
                // changed by Nick
                
                //System.out.println("Query: " + query);            
                Hits  hits = searcher.search(query);
                //printHits(hits);
                if(hits.length()==0)return -1;
                Document d = hits.doc(0);
                String value=d.getField("index").stringValue();
                return Integer.parseInt(value);

          } catch (Exception ex){
              throw new NestableRuntimeException(ex);
          }
    }
    
    private static Object findObject(String searchFor, Object[] objects, String[] objectStrings, boolean isFuzzy)
    {
        if (searchFor == null || objects == null || objectStrings == null)
            return null;

        String queryString;
        try {
            queryString = analyzer.createQuery(searchFor,isFuzzy);
        } catch (IOException e) {
            throw new NestableRuntimeException(e);
        }
        
        int index = findIndex(queryString,objectStrings);
        
        if (index != -1)
            return objects[index];
        else
            return null;
    }
    
    public static ICompilationUnit findCompilationUnit(String tableName, ICompilationUnit[] units, boolean isFuzzy)
    {
        if (tableName == null || units == null)
            return null;

        String[] unitNames = new String[units.length];
        try {
            for (int i = 0; i < units.length; i++) {
                ICompilationUnit unit = units[i];
                if (unit != null)
                    unitNames[i] = unit.getElementName();
            }
            String query = analyzer.createQuery(tableName,false);
            return (ICompilationUnit)findObject(query,units,unitNames,isFuzzy);
        } catch (Exception e) {
            throw new NestableRuntimeException(e);
        }
    }
    //by Nick
    
	//by Nick 17.03.2005
	
    /**
     * discovers collection element class by collection variable name
     * @param varName
     * @param objects
     * @return
     */
    public static IClassSearchInfoHolder findClass(String varName,IClassSearchInfoHolder[] objects, boolean isFuzzy) {
        if (objects == null || varName == null)
            return null;
        try {
            String classQuery;
            if (!isFuzzy)
                classQuery = analyzer.createStemmingQuery(varName);
            else
                classQuery = analyzer.createQuery(varName,isFuzzy);
            String[] classNames = new String[objects.length];
            for (int i = 0; i < classNames.length; i++) {
                classNames[i] = objects[i].getShortName();
            }
            return (IClassSearchInfoHolder) findObject(classQuery, objects,
                    classNames,isFuzzy);
        } 
        catch (IOException e) {
            throw new NestableRuntimeException(e);
        }
            
/*            try {
  	      Directory directory = new RAMDirectory();  
	      IndexWriter writer = new IndexWriter(directory, analyzer, true);
	      for (int j = 0; j < classes.length; j++) {
			    if(classes[j]==null) continue;
		        Document d = new Document();
		        String className = classes[j].getPersistentClass().getShortName();
				d.add(Field.Text("name",analyzer.createStemmingQuery(className)));
		        d.add(Field.Keyword("index",String.valueOf(j)));
		        writer.addDocument(d);
		      }
		      writer.close();

		      Searcher searcher = new IndexSearcher(directory);
		      Query query = QueryParser.parse(classQuery,"name", analyzer);
			  //System.out.println("Query: " + query);			  
		      Hits  hits = searcher.search(query);
			  //printHits(hits);
			  if(hits.length()==0)return null;
	          Document d = hits.doc(0);
			  String value=d.getField("index").stringValue();
			  return classes[Integer.parseInt(value)].getPersistentClass();
	    
	    } catch (Exception e) {
			throw new NestableRuntimeException(e);
        }
*/
            
    }
    //by Nick
    
    
    /**
	 * Return a column with name mathed with a field name. null if there is no matched name
	 * @param heuristic on/off heuristic algorithm to match names like  "fld"  with "field"
	 * */
	public static IDatabaseColumn findColumn(IDatabaseColumn[] cols, String columnQuery, boolean heuristic){
		//by Nick
	    if (cols == null || columnQuery == null)
		    return null;
	    //by Nick
		try{
			return findColumn(cols,analyzer.createQuery(columnQuery,heuristic));
		} catch(Exception ex){
			throw new NestableRuntimeException(ex);
		}
	}
	/**
	 * Return a column that matched a query. null if there is no matched name
	 * @param columnQuery query to find a column. ex: "class discriminator type"
	 * */
	public static IDatabaseColumn findColumn(IDatabaseColumn[] cols, String columnQuery){
		//added by Nick 20.04.2005
		if (cols == null || columnQuery == null)
			return null;
		//by Nick

        //changed and old code commented by Nick 27.05.2005
        String[] columnNames = new String[cols.length];
        for (int i = 0; i < cols.length; i++) {
            IDatabaseColumn column = cols[i];
            if (column != null)
                columnNames[i] = column.getName();
        }
        return (IDatabaseColumn)findObject(columnQuery,cols,columnNames,false);
        
/*        try{
	      Directory directory = new RAMDirectory();  
		  JavaCodeAnalyzer analyzer = new JavaCodeAnalyzer();
	      IndexWriter writer = new IndexWriter(directory, analyzer, true);
	      for (int j = 0; j < cols.length; j++) {
			    if(cols[j]==null) continue;
		        Document d = new Document();
		        d.add(Field.Text("name",cols[j].getName()));
		        d.add(Field.Keyword("index",String.valueOf(j)));
		        writer.addDocument(d);
				//analyzer.displayTokens(cols[j].getName());
		      }
		      writer.close();

		      Searcher searcher = new IndexSearcher(directory);
		      Query query = QueryParser.parse(columnQuery,"name", analyzer);
			  //System.out.println("Query: " + query);			  
		      Hits  hits = searcher.search(query);
			  //printHits(hits);
			  if(hits.length()==0)return null;
	          Document d = hits.doc(0);
			  String value=d.getField("index").stringValue();
			  return cols[Integer.parseInt(value)];

		} catch (Exception ex){
			throw new NestableRuntimeException(ex);
		}
*/	
    //by Nick    
    }
	
	//added by Nick 30.05.2005
    public static final String ANY = new String("*");
    //by Nick
    
    private static abstract class PersistentFieldFilter
    {
        abstract boolean isConformField(IPersistentField field);
        
        private static class PersistentFieldHibTypeFilter extends PersistentFieldFilter
        {
            private Type theType;
            PersistentFieldHibTypeFilter(Type hibType)
            {
                this.theType = hibType;
            }

            boolean isConformField(IPersistentField field) {
                return TypeUtils.canFieldStoreHibType(field.getType(),theType);
            }
        }
        
        private static class PersistentFieldJavaTypesSetFilter extends PersistentFieldFilter
        {
            private Set javaTypesSet;
            PersistentFieldJavaTypesSetFilter(Set javaTypesSet)
            {
                this.javaTypesSet = javaTypesSet;
            }

            boolean isConformField(IPersistentField field) {
                return javaTypesSet.contains(field.getType());
            }
            
        }

        private static class PersistentFieldJavaTypeFilter extends PersistentFieldFilter
        {
            private String javaType;
            PersistentFieldJavaTypeFilter(String javaType)
            {
                this.javaType = javaType;
            }

            boolean isConformField(IPersistentField field) {
                return javaType.equals(field.getType());
            }
            
        }
    }

    private static IPersistentField findUnMappedField(IPersistentField fields[], String fieldQuery, PersistentFieldFilter filteredBy, boolean isFuzzy)
    {
        if (fields == null)
            return null;
        
        ArrayList<IPersistentField> selectedFields = new ArrayList<IPersistentField>();
        
        for (int i = 0; i < fields.length; i++) {
            IPersistentField field = fields[i];
            if (field == null)
                continue;
            if (field.getMapping() != null)
                continue;
            
            if (filteredBy.isConformField(field))
            {
                selectedFields.add(field);
                if (fieldQuery == ANY)
                    break;
            }
        }
        
        if (fieldQuery == ANY)
        {
            if (selectedFields.size() != 0)
                return (IPersistentField) selectedFields.get(0);
            else
                return null;
        }
        
        return findField((IPersistentField[]) selectedFields.toArray(new IPersistentField[0]),fieldQuery,isFuzzy);
    }
    
    public static IPersistentField findUnMappedFieldByType(IPersistentField fields[], String fieldQuery, Set javaTypesSet, boolean isFuzzy)
    {
        return findUnMappedField(fields,fieldQuery,new PersistentFieldFilter.PersistentFieldJavaTypesSetFilter(javaTypesSet),isFuzzy);
    }

    public static IPersistentField findUnMappedFieldByType(IPersistentField fields[], String fieldQuery, Type hibType, boolean isFuzzy)
    {
        return findUnMappedField(fields,fieldQuery,new PersistentFieldFilter.PersistentFieldHibTypeFilter(hibType),isFuzzy);
    }

    public static IPersistentField findUnMappedFieldByType(IPersistentField fields[], String fieldQuery, String javaType, boolean isFuzzy)
    {
        return findUnMappedField(fields,fieldQuery,new PersistentFieldFilter.PersistentFieldJavaTypeFilter(javaType),isFuzzy);
    }
    
    
	/**
	 * Return a field that matched a query
	 * @param fieldQuery query to find a field. ex: "id key pk identifier"
	 * */
	public static IPersistentField findField(IPersistentField fields[], String fieldQuery, boolean isFuzzy){
		//added by Nick 20.04.2005
		if (fields == null || fieldQuery == null)
			return null;
		//by Nick
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            IPersistentField field = fields[i];
            if (field != null)
                fieldNames[i] = field.getName();
        }
        
        return (IPersistentField)findObject(fieldQuery,fields,fieldNames, isFuzzy);
        
/*		try{
		      Directory directory = new RAMDirectory();  
			  JavaCodeAnalyzer analyzer = new JavaCodeAnalyzer();
		      IndexWriter writer = new IndexWriter(directory, analyzer, true);
		      for (int j = 0; j < fields.length; j++) {
				    if(fields[j]==null) continue;
			        Document d = new Document();
			        d.add(Field.Text("name",fields[j].getName()));
			        d.add(Field.Keyword("index",String.valueOf(j)));
			        writer.addDocument(d);
					//analyzer.displayTokens(cols[j].getName());
			      }
			      writer.close();

			      Searcher searcher = new IndexSearcher(directory);
			      Query query = QueryParser.parse(fieldQuery,"name", analyzer);
				  //System.out.println("Query: " + query);			  
			      Hits  hits = searcher.search(query);
				  //printHits(hits);
				  if(hits.length()==0)return null;
		          Document d = hits.doc(0);
				  String value=d.getField("index").stringValue();
				  return fields[Integer.parseInt(value)];

			} catch (Exception ex){
				throw new NestableRuntimeException(ex);
			}
*/
        
    }

	  public static void main(String[] args) {
		    try {
		      Directory directory = new RAMDirectory();  
			  JavaCodeAnalyzer analyzer = new JavaCodeAnalyzer();
		      IndexWriter writer = new IndexWriter(directory, analyzer, true);

		      String[] docs = {
		        "Tables2",
		        "Tables1",
		        "Tables3"
		        
		      };
		      String[] queries = {
						"table1",
					    "Table2",
		      };
		      for (int j = 0; j < queries.length; j++) {
		    	  System.out.println(findIndex(analyzer.createQuery(queries[j],true),docs));
		      }

		      
		      for (int j = 0; j < docs.length; j++) {
		        Document d = new Document();
		        d.add(Field.Text("contents", docs[j]));
		        writer.addDocument(d);
		      }
		      writer.close();

		      Searcher searcher = new IndexSearcher(directory);
		      
		      Hits hits = null;

		      QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
		      for (int j = 0; j < queries.length; j++) {
		        Query query = parser.parse(analyzer.createQuery(queries[j],true));
		        System.out.println("Query: " + query.toString("contents"));
		        hits = searcher.search(query);
				printHits(hits);
		      }
		      
		      /*for (int j = 0; j < queries.length; j++) {
			        Query query = new FuzzyQuery(new Term("contents",queries[j]));
			        System.out.println("Query: " + query.toString("contents"));
			        hits = searcher.search(query);
					printHits(hits);
		      }*/
		      searcher.close();
		      
		    } catch (Exception e) {
		      System.out.println(" caught a " + e.getClass() +
					 "\n with message: " + e.getMessage());
		    }
			/*PersistentField pf=new PersistentField();
			pf.setName("myField");
			Column col1=new Column();
			col1.setName("FIELD");
			Column col2=new Column();
			col2.setName("MY_BIG_FIELD");
			Column col3=new Column();
			col3.setName("MY_FIELD");
			Column col4=new Column();
			col4.setName("H00_FLD");
			IDatabaseColumn[] cols=new IDatabaseColumn[]{col1, col2, col3};
			IDatabaseColumn[] cols1=new IDatabaseColumn[]{col4};
			System.out.println(findColumn(cols, pf.getName(), false));
			System.out.println(findColumn(cols1, pf.getName(), true));
			
			PersistentField pf1=new PersistentField();
			pf1.setName("parentId");
			PersistentField pf2=new PersistentField();
			pf2.setName("id");
			PersistentField pf3=new PersistentField();
			pf3.setName("m_id");
			IPersistentField[] fields=new IPersistentField[]{pf1, pf2, pf3};
			System.out.println(findField(fields,"id key pk identifier",false));
			*/
		  }
	  static void printHits(Hits hits) throws IOException{
	        System.out.println(hits.length() + " total results");
	        for (int i = 0 ; i < hits.length() && i < 10; i++) {
	          Document d = hits.doc(i);
	          System.out.println(i + " " + hits.score(i) + " " + d.get("name"));
	        }
		  
	  }

}
