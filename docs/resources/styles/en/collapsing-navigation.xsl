<!DOCTYPE xsl:stylesheet>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns="http://www.w3.org/TR/xhtml1/transitional"
	exclude-result-prefixes="#default">
	
	<xsl:template name="subtoc">
		<xsl:param name="toc-context" select="." />
		<xsl:param name="nodes" select="NOT-AN-ELEMENT" />

		<xsl:variable name="toc.mark">
			<xsl:apply-templates mode="toc.mark" select="." />
		</xsl:variable>

		<xsl:variable name="should.collapse.list"
			select="string-length(string($toc.mark)) &gt; 0">
		</xsl:variable>

		<xsl:variable name="toc.on.plus.mark">
			<xsl:choose>
				<xsl:when test="$should.collapse.list">
					<xsl:copy-of select="$toc.mark"></xsl:copy-of>
				</xsl:when>
				<xsl:otherwise>
					<span class="expand_collapse_toc" style="visibility:hidden;"> &#160;</span>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="nodes.plus" select="$nodes | qandaset" />

		<xsl:variable name="subtoc">
			<xsl:element name="{$toc.list.type}">
				<xsl:choose>
					<xsl:when test="$qanda.in.toc != 0">
						<xsl:apply-templates mode="toc"
							select="$nodes.plus">
							<xsl:with-param name="toc-context"
								select="$toc-context" />
						</xsl:apply-templates>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates mode="toc"
							select="$nodes">
							<xsl:with-param name="toc-context"
								select="$toc-context" />
						</xsl:apply-templates>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>
		</xsl:variable>

		<xsl:variable name="depth">
			<xsl:choose>
				<xsl:when test="local-name(.) = 'section'">
					<xsl:value-of select="count(ancestor::section) + 1" />
				</xsl:when>
				<xsl:when test="local-name(.) = 'sect1'">1</xsl:when>
				<xsl:when test="local-name(.) = 'sect2'">2</xsl:when>
				<xsl:when test="local-name(.) = 'sect3'">3</xsl:when>
				<xsl:when test="local-name(.) = 'sect4'">4</xsl:when>
				<xsl:when test="local-name(.) = 'sect5'">5</xsl:when>
				<xsl:when test="local-name(.) = 'refsect1'">1</xsl:when>
				<xsl:when test="local-name(.) = 'refsect2'">2</xsl:when>
				<xsl:when test="local-name(.) = 'refsect3'">3</xsl:when>
				<xsl:when test="local-name(.) = 'simplesect'">
					<!-- sigh... -->
					<xsl:choose>
						<xsl:when test="local-name(..) = 'section'">
							<xsl:value-of
								select="count(ancestor::section)" />
						</xsl:when>
						<xsl:when test="local-name(..) = 'sect1'">
							2
						</xsl:when>
						<xsl:when test="local-name(..) = 'sect2'">
							3
						</xsl:when>
						<xsl:when test="local-name(..) = 'sect3'">
							4
						</xsl:when>
						<xsl:when test="local-name(..) = 'sect4'">
							5
						</xsl:when>
						<xsl:when test="local-name(..) = 'sect5'">
							6
						</xsl:when>
						<xsl:when test="local-name(..) = 'refsect1'">
							2
						</xsl:when>
						<xsl:when test="local-name(..) = 'refsect2'">
							3
						</xsl:when>
						<xsl:when test="local-name(..) = 'refsect3'">
							4
						</xsl:when>
						<xsl:otherwise>1</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="depth.from.context"
			select="count(ancestor::*)-count($toc-context/ancestor::*)" />

		<xsl:variable name="subtoc.list">
			<xsl:choose>
				<xsl:when test="$toc.dd.type = ''">
					<xsl:copy-of select="$subtoc" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="{$toc.dd.type}">
						<xsl:if test="$should.collapse.list">
							<xsl:attribute name="style">display:none;</xsl:attribute>
						</xsl:if>
						<xsl:copy-of select="$subtoc" />
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>


		<xsl:element name="{$toc.listitem.type}">

			<xsl:copy-of select="$toc.on.plus.mark"></xsl:copy-of>
			<xsl:call-template name="toc.line">
				<xsl:with-param name="toc-context"
					select="$toc-context" />
			</xsl:call-template>

			<xsl:if
				test="$toc.listitem.type = 'li'
                  and $toc.section.depth > $depth and 
                  ( ($qanda.in.toc = 0 and count($nodes)&gt;0) or
                    ($qanda.in.toc != 0 and count($nodes.plus)&gt;0) )
                  and $toc.max.depth > $depth.from.context">
				<xsl:copy-of select="$subtoc.list" />
			</xsl:if>
		</xsl:element>
		<xsl:if
			test="$toc.listitem.type != 'li'
                and $toc.section.depth > $depth and 
                ( ($qanda.in.toc = 0 and count($nodes)&gt;0) or
                  ($qanda.in.toc != 0 and count($nodes.plus)&gt;0) )
                and $toc.max.depth > $depth.from.context">
			<xsl:copy-of select="$subtoc.list" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="section|chapter" mode="toc.mark">
		<xsl:variable name="subchapters">
			<xsl:apply-templates select="child::section" mode="toc" />
		</xsl:variable>

		<xsl:if test="string-length(string($subchapters))">
			<xsl:call-template name="toggle.expand.mark" />
			<xsl:call-template name="toggle.collapse.mark" />
		</xsl:if>

	</xsl:template>

	<xsl:template match="*" mode="toc.mark">

	</xsl:template>

	<xsl:template name="user.head.content">
		<xsl:param name="node" select="." />
		<script type="text/javascript" src="script/toggle.js"></script>
	</xsl:template>

	<xsl:template name="toggle.expand.mark">
		<xsl:param name="visible" select="true()"/>
		<span onclick="toc.expand(this)" class="expand_collapse_toc">
			<xsl:call-template name="render.display">
				<xsl:with-param name="visible" select="$visible" />
			</xsl:call-template>
			<xsl:text>+</xsl:text>
		</span>
	</xsl:template>

	<xsl:template name="toggle.collapse.mark">
		<xsl:param name="visible" select="false()"/>
		<span onclick="toc.collapse(this)" class="expand_collapse_toc">
			<xsl:call-template name="render.display">
				<xsl:with-param name="visible" select="$visible" />
			</xsl:call-template>
			<xsl:text>-</xsl:text>
		</span>
	</xsl:template>

	<xsl:template name="render.display">
		<xsl:param name="visible" select="false()"/>
		<xsl:attribute name="style">
			<xsl:if test="not($visible)">display:none;</xsl:if>
		</xsl:attribute>
	</xsl:template>

	
</xsl:stylesheet>
