<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:saxon="http://icl.com/saxon"
  extension-element-prefixes="saxon"
  version="1.0">

  <!-- 
    - generate eclipse help toc from build.xml target 'javadoc' 'group' elements
    - visit target: javadoc
    -   for all 'group' children elements
    -     incorporate 'title' attribute as 'label' in main topic
    -     incorporate tokens in 'packages' attribute  as 'label' and 'href' in subtopics
    -      in case of 'label', strip the '*' from end of package name where it exists
    -      in case of 'href', strip * from end, and replace '.' with '/'
    -     
    -->
  <xsl:template match="/project/target/javadoc">
    <xsl:variable name="rootdir">html</xsl:variable>
    <xsl:text>
</xsl:text>
    <toc label="API Reference">
    <xsl:for-each select="child::group">
      <xsl:text>
      </xsl:text>
      <topic label="{@title}">
        <xsl:for-each select="@packages">
          <xsl:for-each select="saxon:tokenize(.,':')"><xsl:text>
            </xsl:text>
            <topic label="{translate(.,'*','')}" href="{$rootdir}/api/{translate(.,'.*','/')}/package-summary.html" />
          </xsl:for-each>
        </xsl:for-each><xsl:text>
      </xsl:text>
      </topic>
    </xsl:for-each><xsl:text>
</xsl:text>
  </toc>
    <xsl:text>
</xsl:text>
  </xsl:template>


  <xsl:template match="*|@*|text()">
    <xsl:apply-templates/>
  </xsl:template>


</xsl:stylesheet>
