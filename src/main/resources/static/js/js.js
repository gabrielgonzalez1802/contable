function addEvents(){
	
	var clienteSeleccionado = $("#clienteSeleccionado").select2({
	    theme: 'bootstrap4',
	});
	
	//Prestamos
	$("#prestamos").click(function(e){
		e.preventDefault();
		$("#contenido").load("/clientes/buscarCliente",function(data){
			console.log("Lista de clientes");
			addEvents();
		});
	});
	
	//Buscar cliente
	$("#buscarPorDocumento").on("keyup", function(e) {
		e.preventDefault();
	     if(e.which == 13){
	    	 var tipoDocumento = "";
	    	 var carpeta = $("#carpetaId").val();
	    	 var item = $("#buscarPorDocumento").val();
		     if($("#cedula").is(':checked')){
		    	 tipoDocumento = "cedula";
		     }else if($("#otro").is(':checked')){
		    	 tipoDocumento = "otro";
		     }
		     var longitud = item.length;
		     if(longitud > 1){
			     $("#contenido").load("/clientes/getInfoCliente",
					{
					   	'carpeta': carpeta,
					   	'tipoDocumento': tipoDocumento,
						'item': item
					},
				function(data){
					console.log("Buscar clientes");
					if($("#msg").val()== "No se encontro el cliente"){
						 Swal.fire({
								title : 'Alerta!',
								text : 'No se encontro al cliente',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
					}
					addEvents();
				});
		     }
	     }
	});
	
	$("input[name='tipoDocumentoBusqueda']").change(function(){
		 if($("#cedula").is(':checked')){
	    	 $('#buscarPorDocumento').attr('maxlength', 13);
	     }else if($("#otro").is(':checked')){
	    	 $('#buscarPorDocumento').attr('maxlength', 30);
	     }
		$('#buscarPorDocumento').val("");
	});
	
	$("#buscarPorNombre").on("keyup", function(e) {
		e.preventDefault();
	     if(e.which == 13){
	    	var item = $("#buscarPorNombre").val();
	    	var carpeta = $("#carpetaId").val();
	    	var longitud = item.length;
		     if(longitud > 1){
			     $("#contenido").load("/clientes/getInfoCliente",
					{
					   	'carpeta': carpeta,
					   	'tipoDocumento': '',
						'item': item
					},
				function(data){
					console.log("Buscar clientes");
					if($("#msg").val()== "No se encontro el cliente"){
						 Swal.fire({
								title : 'Alerta!',
								text : 'No se encontro al cliente',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
					}
					addEvents();
				});
		     }
	     }
	});
	
	//Validacion para cedula
	$('#buscarPorDocumento').on('keyup', function(e) { 
		if($("#cedula").is(':checked')){
			var valor = $('#buscarPorDocumento').val();
			var res = valor.match(/^[0-9-]+$/);
			
			if(res == null){
				 $('#buscarPorDocumento').val(valor.substring(0, valor.length - 1));
			}
		}	
	});
	 
	$('#buscarPorDocumento').on('keydown', function(e) { 
		if($("#cedula").is(':checked')){
			var valor = $('#buscarPorDocumento').val();
			var res = valor.match(/^[0-9-]+$/);
				
			if(res == null){
				 $('#buscarPorDocumento').val(valor.substring(0, valor.length - 1));
			}else{
				var keyCode = (window.event) ? e.which : e.keyCode;
				// Si no preciona la tecla de borrar
				if(keyCode !=8 && keyCode != 46){
					   var cant = valor.length;
					   if(cant == 3){
					   	$('#buscarPorDocumento').val(valor+"-");
					   }
						   
					  if(cant == 11){
					   	$('#buscarPorDocumento').val(valor+"-");
				 }
				}
			}
		}
	});
	
	$("#btnCarpeta").click(function(e){
		e.preventDefault();
		Swal.fire({
			  title: 'Ingrese la carpeta',
			  position: 'top',
			  input: 'text',
			  inputAttributes: {
			    autocapitalize: 'off'
			  },
			  showCancelButton: true,
			  confirmButtonText: 'Buscar',
			  cancelButtonText: 'Cancelar',
			  showLoaderOnConfirm: true,
			  preConfirm: (carpeta) => {
				  $.ajax({
			            url: "/carpetas/buscar/"+carpeta,
			            type: "GET",
//			            data: "",
//			            enctype: 'multipart/form-data',
			            processData: false,
			            contentType: false,
			            cache: false,
			            success: function (res) {
				            if(res=="No tienes carpetas creadas"){
								Swal.fire({
									title : 'No tienes carpetas creadas!',
//									text : 'No tienes carpetas creadas',
									position : 'top',
									icon : 'warning',
									confirmButtonText : 'Cool'
								})
								$("#contenido").load("/clientes/buscarCliente",function(data){
									console.log("Lista de clientes");
									addEvents();
								});
							}else{
								Swal.fire({
									title : 'Muy bien!',
									text : 'Carpeta Seleccionada',
									position : 'top',
									icon : 'success',
									confirmButtonText : 'Cool'
								})
								e.preventDefault();
								$("#contenido").load("/clientes/buscarCliente",function(data){
									console.log("Lista de clientes");
									addEvents();
								});
							}
			            },
			            error: function (err) {
			                console.error(err);
			                Swal.fire({
								title : 'Error!',
								text : 'No se pudo completar la operacion, intente mas tarde',
								position : 'top',
								icon : 'error',
								confirmButtonText : 'Cool'
							})
			            }
			        });
			  },
			  allowOutsideClick: () => !Swal.isLoading()
			}).then((result) => {
			  if (result.isConfirmed) {
			    //Correcto
			  }
			})
	});
	
	$("#btnExitCarpeta").click(function(e){
		e.preventDefault();
		$("#contenido").load("/clientes/buscarClienteCarpetaPrincipal",function(data){
			console.log("Lista de clientes");
			addEvents();
		});
	});	
	
	
	//Fin Buscar Cliente
	
	//Buscar cancelCliente
	$("#cancelCliente").click(function(e){
		e.preventDefault();
		$("#contenido").load("/clientes/buscarCliente",function(data){
			console.log("Lista de clientes");
			addEvents();
		});
	});
	
	//Boton de agregar cliente
	$("#agregarCliente").click(function(e){
		e.preventDefault();
		var idCliente = $("#idCliente").val();
		$("#contenido").load("/clientes/agregar",function(data){
			console.log("Formulario agregar clientes");
			addEvents();
		});
	});
	
	//Formulario de clientes
	 $("input[name='tipoDocumento']").click(function(){
		 $("#cedulaCliente").attr('disabled', false);
		 $('#cedulaCliente').val("");
         var tipoDocumento = $("input[name='tipoDocumento']:checked").val();
         if($("#docCedula").is(':checked')) {
        	 $("#doctypeTemp").val("cedula");
        	 $('#cedulaCliente').attr('maxlength', 13);
         }else if($("#docPasaporte").is(':checked')){
        	 $('#cedulaCliente').attr('maxlength', 50);
        	 $("#doctypeTemp").val("pasaporte");
         }
     });
	 
	//Validacion para cedula
	$('#cedulaCliente').on('keyup', function(e) { 
		if($("#docCedula").is(':checked')){
			var valor = $('#cedulaCliente').val();
			var res = valor.match(/^[0-9-]+$/);
			
			if(res == null){
				 $('#cedulaCliente').val(valor.substring(0, valor.length - 1));
			}
		}	
	});
	 
	$('#cedulaCliente').on('keydown', function(e) { 
		if($("#docCedula").is(':checked')){
			var valor = $('#cedulaCliente').val();
			var res = valor.match(/^[0-9-]+$/);
				
			if(res == null){
				 $('#cedulaCliente').val(valor.substring(0, valor.length - 1));
			}else{
				var keyCode = (window.event) ? e.which : e.keyCode;
				// Si no preciona la tecla de borrar
				if(keyCode !=8 && keyCode != 46){
					   var cant = valor.length;
					   if(cant == 3){
					   	$('#cedulaCliente').val(valor+"-");
					   }
						   
					  if(cant == 11){
					   	$('#cedulaCliente').val(valor+"-");
				 }
				}
			}
		}
	});
	
	//Validacion para telefono
	$('#telefonoCliente').on('keydown', function(e) { 
			var valor = $('#telefonoCliente').val();
			var res = valor.match(/^[0-9-]+$/);
			
			if(res == null){
				 $('#telefonoCliente').val(valor.substring(0, valor.length - 1));
			}else{
				var keyCode = (window.event) ? e.which : e.keyCode;
				// Si no preciona la tecla de borrar
				if(keyCode !=8 && keyCode != 46){
					  var cant = valor.length;
					  if(cant == 3){
					   	$('#telefonoCliente').val(valor+"-");
					  }
					  					   
					  if(cant == 7){
					   	$('#telefonoCliente').val(valor+"-");
					  }
				}
			}
	});
	
	//Validacion para celular
	$('#celularCliente').on('keydown', function(e) { 
			var valor = $('#celularCliente').val();
			var res = valor.match(/^[0-9-]+$/);
			
			if(res == null){
				 $('#celularCliente').val(valor.substring(0, valor.length - 1));
			}else{
				var keyCode = (window.event) ? e.which : e.keyCode;
				// Si no preciona la tecla de borrar
				if(keyCode !=8 && keyCode != 46){
					  var cant = valor.length;
					  if(cant == 3){
					   	$('#celularCliente').val(valor+"-");
					  }
					  					   
					  if(cant == 7){
					   	$('#celularCliente').val(valor+"-");
					  }
				}
			}
	});
	
	//Validacion para telefono empresa
	$('#telefonoEmpresaCliente').on('keydown', function(e) { 
			var valor = $('#telefonoEmpresaCliente').val();
			var res = valor.match(/^[0-9-]+$/);
			
			if(res == null){
				 $('#telefonoEmpresaCliente').val(valor.substring(0, valor.length - 1));
			}else{
				var keyCode = (window.event) ? e.which : e.keyCode;
				// Si no preciona la tecla de borrar
				if(keyCode !=8 && keyCode != 46){
					  var cant = valor.length;
					  if(cant == 3){
					   	$('#telefonoEmpresaCliente').val(valor+"-");
					  }
					  					   
					  if(cant == 7){
					   	$('#telefonoEmpresaCliente').val(valor+"-");
					  }
				}
			}
	});
	
    $("#formClient").on("submit", function (e) {
		e.preventDefault();
	        $.ajax({
	            url: "/clientes/guardar/",
	            type: "POST",
	            data: new FormData(this),
	            enctype: 'multipart/form-data',
	            processData: false,
	            contentType: false,
	            cache: false,
	            success: function (res) {
		            if(res=="INSERT"){
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
		               console.log(res);
		               $("#contenido").load("/clientes/",function(data){
		        		console.log("Lista de clientes");
		        		addEvents();
		        	});
	            },
	            error: function (err) {
	                console.error(err);
	                Swal.fire({
						title : 'Error!',
						text : 'No se pudo completar la operacion, intente mas tarde',
						position : 'top',
						icon : 'error',
						confirmButtonText : 'Cool'
					})
	            }
	        });
	});
	//Fin formulario de clientes
	
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


//	$("#listadoClientes").click(function(e){
//		e.preventDefault();
//		$("#contenido").load("/clientes/",function(data){
//			console.log("Lista de clientes");
//			addEvents();
//		});
//	});


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

//	/** Fin Prestamos **/

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

function seleccionarCliente(id){
	 $("#contenido").load("/clientes/getInfoCliente/"+id,
			function(data){
				console.log("Buscar clientes");
				if($("#msg").val()== "No se encontro el cliente"){
					 Swal.fire({
							title : 'Alerta!',
							text : 'No se encontro al cliente',
							position : 'top',
							icon : 'warning',
							confirmButtonText : 'Cool'
						})
				}
				addEvents();
			});
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
