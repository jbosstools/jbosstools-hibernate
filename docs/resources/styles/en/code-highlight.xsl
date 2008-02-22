<?xml version="1.0"?>

<!--

-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0"
                xmlns="http://www.w3.org/TR/xhtml1/transitional"
                exclude-result-prefixes="#default"
                xmlns:rf="java:org.richfaces.highlight.XhtmlRendererFactory"
                >

	<xsl:template match="programlisting[@role='XML']|programlisting[@role='JAVA']|programlisting[@role='XHTML']|programlisting[@role='JSP']|programlisting[@role='CSS']">
		
		<xsl:variable name="role">
			<xsl:value-of select="s:toUpperCase(string(@role))" xmlns:s="java:java.lang.String"/>
		</xsl:variable>
		<xsl:variable name="child.content">
			<xsl:apply-templates/>
		</xsl:variable>
		<xsl:variable name="factory" select="rf:instance()"/>
		<xsl:variable name="hiliter" select="rf:getRenderer($factory, string($role))"/>
		<pre class="{$role}">	
			<xsl:choose>
				<xsl:when test="$hiliter">
					<xsl:value-of select="jhr:highlight($hiliter, $role, string($child.content), 'UTF-8', true())"
						xmlns:jhr="com.uwyn.jhighlight.renderer.Renderer" disable-output-escaping="yes"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$child.content"/>
				</xsl:otherwise>
			</xsl:choose>
		</pre>
		
	</xsl:template>
                
</xsl:stylesheet>