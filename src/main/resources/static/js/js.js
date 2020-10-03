function addEvents(){
	
	//Buscar cliente
	$("#prestamos").click(function(e){
		e.preventDefault();
		$("#contenido").load("/clientes/buscarCliente",function(data){
			console.log("Lista de clientes");
			addEvents();
		});
	});
//
//	$("#navListaCliente").click(function(e){
//		e.preventDefault();
//		$("#contenido").load("/clientes/",function(data){
//			console.log("Lista de clientes");
//			addEvents();
//		});
//	});
//	
//	$("#prestamos").click(function(e){
//		e.preventDefault();
//		$("#contenido").load("/clientes/",function(data){
//			console.log("Lista de clientes");
//			addEvents();
//		});
//	});
//	
//	$("#addCustomer").click(function(e){
//		e.preventDefault();
//		$("#contenido").load("/clientes/agregar",function(data){
//			console.log("Agregar Cliente");
//			addEvents();
//		});
//	});
//	
//	$("#listadoClientes").click(function(e){
//		e.preventDefault();
//		$("#contenido").load("/clientes/",function(data){
//			console.log("Lista de clientes");
//			addEvents();
//		});
//	});
//
//    $("#formClient").on("submit", function (e) {
//		e.preventDefault();
//	        $.ajax({
//	            url: "/clientes/guardar/",
//	            type: "POST",
//	            data: new FormData(this),
//	            enctype: 'multipart/form-data',
//	            processData: false,
//	            contentType: false,
//	            cache: false,
//	            success: function (res) {
//		            if(res=="INSERT"){
//						Swal.fire({
//							title : 'Muy bien!',
//							text : 'Registro guardado',
//							position : 'top',
//							icon : 'success',
//							confirmButtonText : 'Cool'
//						})
//					}else{
//						Swal.fire({
//							title : 'Muy bien!',
//							text : 'Registro Modificado',
//							position : 'top',
//							icon : 'success',
//							confirmButtonText : 'Cool'
//						})
//					}
//		               console.log(res);
//		               $("#contenido").load("/clientes/",function(data){
//		        		console.log("Lista de clientes");
//		        		addEvents();
//		        	});
//	            },
//	            error: function (err) {
//	                console.error(err);
//	                Swal.fire({
//						title : 'Error!',
//						text : 'No se pudo completar la operacion, intente mas tarde',
//						position : 'top',
//						icon : 'error',
//						confirmButtonText : 'Cool'
//					})
//	            }
//	        });
//	});
//	
//	$("#navListaCliente").click(function(e){
//		e.preventDefault();
//		$("#contenido").load("/clientes/",function(data){
//			console.log("Lista de clientes");
//			addEvents();
//		});
//	});
//	
//	/** Prestamos **/
//	$("#navListaPrestamo").click(function(e){
//		e.preventDefault();
//		$("#contenido").load("/prestamos/",function(data){
//			console.log("Lista de prestamos");
//			addEvents();
//		});
//	});
//	 
//	$("#addPrestamo").click(function(e){
//		e.preventDefault();
//		$("#contenido").load("/prestamos/agregar",function(data){
//			console.log("Agregar Prestamo");
//			addEvents();
//		});
//	});
//	
//	//Validaciones para el formulario de cliente
//	$("#tipoDocumento").change(function(e){
//		console.log("Tipo de documento");
//		$('#cedulaCliente').val("");
//		if($("#tipoDocumento").val()=="Cedula"){
//			$('#cedulaCliente').attr('maxlength', 13);
//		}else{
//			$('#cedulaCliente').attr('maxlength', 50);
//		}
//	});
//	
//	//Validacion para cedula
//	$('#cedulaCliente').on('keydown', function(e) { 
//		if($("#tipoDocumento").val()=="Cedula"){
//			var valor = $('#cedulaCliente').val();
//			var res = valor.match(/^[0-9-]+$/);
//			
//			if(res == null){
//				 $('#cedulaCliente').val(valor.substring(0, valor.length - 1));
//			}else{
//				var keyCode = (window.event) ? e.which : e.keyCode;
//				// Si no preciona la tecla de borrar
//				if(keyCode !=8 && keyCode != 46){
//					   var cant = valor.length;
//					   if(cant == 3){
//					   	$('#cedulaCliente').val(valor+"-");
//					   }
//					   
//					  if(cant == 11){
//					   	$('#cedulaCliente').val(valor+"-");
//				 }
//				}
//			}
//		}
//	});
//	
//	//Validacion para telefono
//	$('#telefonoCliente').on('keydown', function(e) { 
//			var valor = $('#telefonoCliente').val();
//			var res = valor.match(/^[0-9-]+$/);
//			
//			if(res == null){
//				 $('#telefonoCliente').val(valor.substring(0, valor.length - 1));
//			}else{
//				var keyCode = (window.event) ? e.which : e.keyCode;
//				// Si no preciona la tecla de borrar
//				if(keyCode !=8 && keyCode != 46){
//					  var cant = valor.length;
//					  if(cant == 3){
//					   	$('#telefonoCliente').val(valor+"-");
//					  }
//					  					   
//					  if(cant == 7){
//					   	$('#telefonoCliente').val(valor+"-");
//					  }
//				}
//			}
//	});
//	
//	//Validacion para celular
//	$('#celularCliente').on('keydown', function(e) { 
//			var valor = $('#celularCliente').val();
//			var res = valor.match(/^[0-9-]+$/);
//			
//			if(res == null){
//				 $('#celularCliente').val(valor.substring(0, valor.length - 1));
//			}else{
//				var keyCode = (window.event) ? e.which : e.keyCode;
//				// Si no preciona la tecla de borrar
//				if(keyCode !=8 && keyCode != 46){
//					  var cant = valor.length;
//					  if(cant == 3){
//					   	$('#celularCliente').val(valor+"-");
//					  }
//					  					   
//					  if(cant == 7){
//					   	$('#celularCliente').val(valor+"-");
//					  }
//				}
//			}
//	});
//	
//	//Validacion para telefono empresa
//	$('#telefonoEmpresaCliente').on('keydown', function(e) { 
//			var valor = $('#telefonoEmpresaCliente').val();
//			var res = valor.match(/^[0-9-]+$/);
//			
//			if(res == null){
//				 $('#telefonoEmpresaCliente').val(valor.substring(0, valor.length - 1));
//			}else{
//				var keyCode = (window.event) ? e.which : e.keyCode;
//				// Si no preciona la tecla de borrar
//				if(keyCode !=8 && keyCode != 46){
//					  var cant = valor.length;
//					  if(cant == 3){
//					   	$('#telefonoEmpresaCliente').val(valor+"-");
//					  }
//					  					   
//					  if(cant == 7){
//					   	$('#telefonoEmpresaCliente').val(valor+"-");
//					  }
//				}
//			}
//	});
//	
//	/** Fin Prestamos **/
//	
//	$('#tabla').DataTable({
//		"scrollY": "400px",
//		    "language": {
//		        "sProcessing":    "Procesando...",
//		        "sLengthMenu":    "Mostrar _MENU_ registros",
//		        "sZeroRecords":   "No se encontraron resultados",
//		        "sEmptyTable":    "Ningún dato disponible en esta tabla",
//		        "sInfo":          "Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros",
//		        "sInfoEmpty":     "Mostrando registros del 0 al 0 de un total de 0 registros",
//		        "sInfoFiltered":  "(filtrado de un total de _MAX_ registros)",
//		        "sInfoPostFix":   "",
//		        "sSearch":        "Buscar:",
//		        "sUrl":           "",
//		        "sInfoThousands":  ",",
//		        "sLoadingRecords": "Cargando...",
//		        "oPaginate": {
//		            "sFirst":    "Primero",
//		            "sLast":    "Último",
//		            "sNext":    "Siguiente",
//		            "sPrevious": "Anterior"
//		        },
//		        "oAria": {
//		            "sSortDescending": ": Activar para ordenar la columna de manera descendente"
//		        }
//		    }
//	});
}

function modificarCliente(id){
	$("#contenido").load("/clientes/modificar/"+id,function(data){
		console.log("Modificar Cliente");
		addEvents();
	});
}

function amortizar(){
	 $.post("/prestamos/amortizar",$("#formulario_prestamo").serialize(),
		function(data){
			console.log("Amortizar");
			$("#cuerpo_amortizacion").replaceWith(data);
			addEvents();
	 	});
}

function calCuota(){
	var monto=$("#monto").val();
	var pagos=$("#pagos").val();
	var tasa=$("#tasa").val();
	
  if(parseFloat(monto) > 0 && parseFloat(pagos) && parseFloat(tasa)){
	  $.post("/prestamos/calculoCuota",$("#formulario_prestamo").serialize(),
			function(data){
				console.log("Calculo Cuota");
				$("#calculosCuota").replaceWith(data);
				$("#valor_cuota").val($("#responseCuota").val());
			    $("#valor_interes").val($("#responseInteres").val());   
				$("#total_pagar").val($("#responseTotalPagar").val());
				
				if(parseFloat(tasa) > 0 && parseFloat(pagos) > 0 && parseFloat(tasa)){
					console.log("entre en amortizar");
					amortizar();
				}
		});
  }
}

function calTasa(){
	var monto=$("#monto").val();
	var pagos=$("#pagos").val();
	var cuota=$("#valor_cuota").val();
	
	if(monto && pagos && cuota){ 
		  $.post("/prestamos/calculoTasa",$("#formulario_prestamo").serialize(),
				function(data){
					console.log("Calculo Tasa");
					$("#calculosTasa").replaceWith(data);
				    $("#valor_interes").val($("#responseTasaInteres").val()); 
				    $("#tasa").val($("#responseTasa").val());
					$("#total_pagar").val($("#responseTasaTotalPagar").val());
					amortizar();
			});
	  }
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
				addEvents();
			});
		  }
		})
}
