function addEventClient(){
	
	$("#prestamos").click(function(e){
		e.preventDefault();
		$("#contenido").load("/clientes/",function(data){
			console.log("Lista de clientes");
			addEventClient();
		});
	});
	
	$("#addCustomer").click(function(e){
		e.preventDefault();
		$("#contenido").load("/clientes/agregar",function(data){
			console.log("Agregar Cliente");
			addEventClient();
		});
	});

	$("#listadoClientes").click(function(e){
		e.preventDefault();
		$("#contenido").load("/clientes/",function(data){
			console.log("Lista de clientes");
			addEventClient();
		});
	});

	$("#formClient").submit(function(e){
		e.preventDefault();
		 $.post("/clientes/guardar/", $("#formClient").serialize(),
			function(data, status){
				$('#grupoResponseCliente').replaceWith(data);
				var response = $('#responseCliente').val();
				if(response=="INSERT"){
						Swal.fire({
							title : 'Muy bien!',
							text : 'Registro guardado',
							position : 'top',
							icon : 'success',
							confirmButtonText : 'Cool'
						})
					}else{
						Swal.fire({
							title : 'Muy bien!',
							text : 'Registro Modificado',
							position : 'top',
							icon : 'success',
							confirmButtonText : 'Cool'
						})
					}
				$("#contenido").load("/clientes/",function(data){
					console.log("Lista de clientes");
					addEventClient();
				});
			});		
	});
	
	$('#tabla').DataTable({
		"scrollY": "400px",
		    "language": {
		        "sProcessing":    "Procesando...",
		        "sLengthMenu":    "Mostrar _MENU_ registros",
		        "sZeroRecords":   "No se encontraron resultados",
		        "sEmptyTable":    "Ningún dato disponible en esta tabla",
		        "sInfo":          "Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros",
		        "sInfoEmpty":     "Mostrando registros del 0 al 0 de un total de 0 registros",
		        "sInfoFiltered":  "(filtrado de un total de _MAX_ registros)",
		        "sInfoPostFix":   "",
		        "sSearch":        "Buscar:",
		        "sUrl":           "",
		        "sInfoThousands":  ",",
		        "sLoadingRecords": "Cargando...",
		        "oPaginate": {
		            "sFirst":    "Primero",
		            "sLast":    "Último",
		            "sNext":    "Siguiente",
		            "sPrevious": "Anterior"
		        },
		        "oAria": {
		            "sSortDescending": ": Activar para ordenar la columna de manera descendente"
		        }
		    }
	});
}

function modificarCliente(id){
	$("#contenido").load("/clientes/modificar/"+id,function(data){
		console.log("Modificar Cliente");
		addEventClient();
	});
}

function eliminarCliente(id){
	Swal.fire({
		  title: 'Esta seguro?',
		  text: "Esta accion es irreversible!",
		  icon: 'warning',
		  position: 'top',
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  confirmButtonText: 'Si, Eliminar!',
		  cancelButtonText: 'Cancelar',
		}).then((result) => {
		  if (result.value) {
		    Swal.fire({
			  icon: 'success',
			  title: 'Muy bien!',
			  text: 'El registro se borrara.!',
			  position: 'top'
			})
		    
			$("#contenido").load("/clientes/eliminar/"+id,function(data){
				console.log("Eliminar Cliente");
				addEventClient();
			});
		  }
		})
}