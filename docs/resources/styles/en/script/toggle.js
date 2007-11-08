function dbToggle(node, expandText, collapseText) {
	var dt = node.parentNode;
	if (dt.nodeName.toLowerCase() == 'dt') {
		var dd =  dt.nextSibling;
		
		if (dd && dd.nodeName.toLowerCase() == 'dd') {
			
			if (dd.style && dd.style.display == 'none') {
				dd.style.display = '';
				node.innerHTML = collapseText;
			} else {
				dd.style.display = 'none';
				node.innerHTML = expandText;
			}
		
		}
		
	}
	
}

var toc = {
	expand: function(node) {
		toc.show(toc.findDD(node))
		toc.hide(node);
		toc.show(node.nextSibling);
	}, 
	collapse : function(node) {
		toc.hide(toc.findDD(node))
		toc.hide(node);
		toc.show(node.previousSibling);
	}, 
	findDD : function(node) {
		return node.parentNode.nextSibling;
	},
	
	hide: function(node) {
		node.style.display = "none";
	},
	show: function(node) {
		node.style.display = "";
	}
}; 