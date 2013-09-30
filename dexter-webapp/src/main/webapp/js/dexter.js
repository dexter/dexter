function annotateText(){
	var textarea = document.getElementById('inputtext');
	text = textarea.value
	
	//alert("annotate: "+text.substring(0,100)+"...")
	//
	$.getJSON("./rest/annotate?text="+text,
	function(data) {
			//alert(val)
			annotate = data.text
			document.getElementById("result").innerHTML = annotate;
			document.getElementById('input').style.display="none";
			document.getElementById('output').style.display="block";
		
		}
	);

}



function manage(id){

	//
	$.getJSON("./rest/get-desc?id="+id,
	function(data) {
			//alert(val)
			title = data.title
			document.getElementById("info").innerHTML = "<strong>"+title+"</strong><div id='desc'>"
			if (typeof data.image != 'undefined' ){
				document.getElementById("info").innerHTML += data.image
			}
			document.getElementById("info").innerHTML += "<p>"+data.description.substring(0,1000)+"..."+"</p></div>"
			
			
	}
	);

}

function reset(){
	document.getElementById('output').style.display="none";
	document.getElementById('input').style.display="block";
}