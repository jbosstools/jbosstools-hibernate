<!DOCTYPE xsl:stylesheet>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns="http://www.w3.org/TR/xhtml1/transitional"
	exclude-result-prefixes="#default">
	
  <xsl:template match="programlisting[@role='XML']|programlisting[@role='JAVA']|programlisting[@role='XHTML']|programlisting[@role='JSP']">
    <xsl:variable name="kidz">
      <xsl:apply-templates></xsl:apply-templates>
    </xsl:variable>
    <pre class="{@role}">
      <xsl:value-of 
        select="javahl:highlight(string($kidz), attribute::role)"
        xmlns:javahl="java:com.exadel.docbook.colorer.HighLighter"
        disable-output-escaping="yes"/>
    </pre>
  </xsl:template>
	
</xsl:stylesheet>
