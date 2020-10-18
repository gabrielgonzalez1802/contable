function addEvents(){
	
/************************************************** Configuraciones Iniciales **********************************************/
	
    $('[data-toggle="tooltip"]').tooltip();
    
	var clienteSeleccionado = $("#clienteSeleccionado").select2({
	    theme: 'bootstrap4',
	});
		
	//$('#tabla').DataTable({
	//"scrollY": "400px",
//	    "language": {
//	        "sProcessing":    "Procesando...",
//	        "sLengthMenu":    "Mostrar _MENU_ registros",
//	        "sZeroRecords":   "No se encontraron resultados",
//	        "sEmptyTable":    "Ningún dato disponible en esta tabla",
//	        "sInfo":          "Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros",
//	        "sInfoEmpty":     "Mostrando registros del 0 al 0 de un total de 0 registros",
//	        "sInfoFiltered":  "(filtrado de un total de _MAX_ registros)",
//	        "sInfoPostFix":   "",
//	        "sSearch":        "Buscar:",
//	        "sUrl":           "",
//	        "sInfoThousands":  ",",
//	        "sLoadingRecords": "Cargando...",
//	        "oPaginate": {
//	            "sFirst":    "Primero",
//	            "sLast":    "Último",
//	            "sNext":    "Siguiente",
//	            "sPrevious": "Anterior"
//	        },
//	        "oAria": {
//	            "sSortDescending": ": Activar para ordenar la columna de manera descendente"
//	        }
//	    }
	//});
	
/*********************************************** Fin Configuraciones Iniciales **********************************************/	

/******************************************************* Clientes ***********************************************************/	
	//Buscar cliente
	$("#buscarPorDocumento").on("keyup", function(e) {
		e.preventDefault();
		e.stopImmediatePropagation();
		ocultarDetalleAmortizacion();
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
					if($("#tipoDocumentoAcct").val() == 'cedula'){
						$("#cedula").attr('checked', 'checked');
						$("#otro").attr('checked', false);
					}else{
						$("#otro").attr('checked', 'checked');
						$("#cedula").attr('checked', false);
					}
					if($("#msg").val()== "No se encontro el cliente"){
						 Swal.fire({
								title : 'Alerta!',
								text : 'No se encontro al cliente',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
							$("#cedula").attr('checked', 'checked');
							$("#otro").attr('checked', false);
					}
					addEvents();
				});
		     }
	     }
	});
	
	$("#btnDetalleCliente").click(function(e){
		$('#modalInfoCliente').modal('show');
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
		e.stopImmediatePropagation();
		ocultarDetalleAmortizacion();
	     if(e.which == 13){
	    	var item = $("#buscarPorNombre").val();
	    	var carpeta = $("#carpetaId").val();
	    	var longitud = item.length;
		     if(longitud > 1){
			     $("#contenido").load("/clientes/getInfoCliente",
					{
					   	'carpeta': carpeta,
					   	'tipoDocumento': '0',
						'item': item
					},
				function(data){
					console.log("Buscar clientes");
					if($("#tipoDocumentoAcct").val() == 'cedula'){
						$("#cedula").attr('checked', 'checked');
						$("#otro").attr('checked', false);
					}else{
						$("#otro").attr('checked', 'checked');
						$("#cedula").attr('checked', false);
					}	
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
		e.stopImmediatePropagation();
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
									ocultarDetalleAmortizacion();
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
									ocultarDetalleAmortizacion();
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
		e.stopImmediatePropagation();
		$("#contenido").load("/clientes/buscarClienteCarpetaPrincipal",function(data){
			console.log("Lista de clientes");
			ocultarDetalleAmortizacion();
			addEvents();
		});
	});	
		
	//Buscar cancelCliente
	$("#cancelCliente").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		$("#contenido").load("/clientes/buscarCliente",function(data){
			console.log("Lista de clientes");
			ocultarDetalleAmortizacion();
			addEvents();
		});
	});
	
	//Boton de agregar cliente
	$("#agregarCliente").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var idCliente = $("#idCliente").val();
		$("#contenido").load("/clientes/agregar",function(data){
			console.log("Formulario agregar clientes");
			ocultarDetalleAmortizacion();
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
        	 $("#doctypeTemp").val("otro");
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
		e.stopImmediatePropagation();
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

/******************************************************* Fin Clientes ************************************************************/	
    
/******************************************************* Prestamos ***********************************************************/
	$("#prestamos").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		$("#contenido").load("/clientes/buscarCliente",function(data){
			ocultarDetalleAmortizacion();
			console.log("Lista de clientes");
			addEvents();
		});
	});
	
	$("#btnCarpetaPrestamo").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
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
			            processData: false,
			            contentType: false,
			            cache: false,
			            success: function (res) {
				            if(res=="No tienes carpetas creadas"){
								Swal.fire({
									title : 'No tienes carpetas creadas!',
									position : 'top',
									icon : 'warning',
									confirmButtonText : 'Cool'
								})

							}else{
								Swal.fire({
									title : 'Muy bien!',
									text : 'Carpeta Seleccionada',
									position : 'top',
									icon : 'success',
									confirmButtonText : 'Cool'
								})
							}
							$("#buscadorAgregarPrestamo").load("/prestamos/actualizarCarpeta",function(data){
								console.log("Actualizar carpeta");
								if($("#tipoDocumentoAcctPrestamo").val() == 'cedula'){
									$("#cedulaPrestamo").attr('checked', 'checked');
									$("#otroPrestamo").attr('checked', false);
								}else{
									$("#otroPrestamo").attr('checked', 'checked');
									$("#cedulaPrestamo").attr('checked', false);
								}
								//Recargar combo de cuentas
								var carpeta = $("#carpetaIdPrestamo").val();
								$("#id_cuenta").load("/prestamos/actualizarCuentas/"+carpeta,function(data){
									console.log("Actualizar cuenta");
									addEvents();
									return false;
								});
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
			  },
			  allowOutsideClick: () => !Swal.isLoading()
			}).then((result) => {
			  if (result.isConfirmed) {
			    //Correcto
			  }
			})
	});
    
	$("#btnExitCarpetaPrestamo").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		$("#buscadorAgregarPrestamo").load("/prestamos/actualizarCarpetaPrincial",function(data){
			console.log("Actualizar carpeta");
			if($("#tipoDocumentoAcctPrestamo").val() == 'cedula'){
				$("#cedulaPrestamo").attr('checked', 'checked');
				$("#otroPrestamo").attr('checked', false);
			}else{
				$("#otroPrestamo").attr('checked', 'checked');
				$("#cedulaPrestamo").attr('checked', false);
			}
			//Recargar combo de cuentas
			var carpeta = $("#carpetaIdPrestamo").val();
			$("#id_cuenta").load("/prestamos/actualizarCuentas/"+carpeta,function(data){
				console.log("Actualizar cuenta");
				addEvents();
				return false;
			});
		});
	});	
	
    $("#agregarPrestamo").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		//Ocultamos la seccion de detalle de amortizacion
		mostrarDetalleAmortizacion();
		$("#contenido").load("/prestamos/agregar",function(data){
			console.log("Agregar Prestamo");
			if($("#msg").val()=="NOCLIENTE"){
				 Swal.fire({
						title : 'Alerta!',
						text : 'Debe seleccionar un cliente',
						position : 'top',
						icon : 'warning',
						confirmButtonText : 'Cool'
					})
					setTimeout(function() {
						$("#contenido").load("/clientes/buscarCliente",function(data){
							console.log("Lista de clientes");
							addEvents();
						});	
					}, 1000);
			}else{
				if($("#tipoDocumentoAcctPrestamo").val() == 'cedula'){
					$("#cedulaPrestamo").attr('checked', 'checked');
					$("#otroPrestamo").attr('checked', false);
				}else{
					$("#otroPrestamo").attr('checked', 'checked');
					$("#cedulaPrestamo").attr('checked', false);
				}
			}
			addEvents();
		});
	});
	
	$("#cancelarFormPrestamo").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
			$("#contenido").load("/clientes/buscarCliente",function(data){
					console.log("Lista de clientes");
					addEvents();
			});	
			addEvents();
	});
	
	$("input[name='tipoDocumentoBusquedaPrestamo']").change(function(){
		 if($("#cedulaPrestamo").is(':checked')){
	    	 $('#buscarPorDocumentoPrestamo').attr('maxlength', 13);
	     }else if($("#otroPrestamo").is(':checked')){
	    	 $('#buscarPorDocumentoPrestamo').attr('maxlength', 30);
	     }
		$('#buscarPorDocumentoPrestamo').val("");
	});
	
	$('#buscarPorDocumentoPrestamo').on('keydown', function(e) { 
		if($("#cedulaPrestamo").is(':checked')){
			var valor = $('#buscarPorDocumentoPrestamo').val();
			var res = valor.match(/^[0-9-]+$/);
				
			if(res == null){
				 $('#buscarPorDocumentoPrestamo').val(valor.substring(0, valor.length - 1));
			}else{
				var keyCode = (window.event) ? e.which : e.keyCode;
				// Si no preciona la tecla de borrar
				if(keyCode !=8 && keyCode != 46){
					   var cant = valor.length;
					   if(cant == 3){
					   	$('#buscarPorDocumentoPrestamo').val(valor+"-");
					   }
						   
					  if(cant == 11){
					   	$('#buscarPorDocumentoPrestamo').val(valor+"-");
					  }
				}
			}
		}
	});
	
	$("#buscarPorDocumentoPrestamo").on("keyup", function(e) {
		e.preventDefault();
		e.stopImmediatePropagation();
	     if(e.which == 13){
	    	 var tipoDocumento = "";
	    	 var carpeta = $("#carpetaIdPrestamo").val();
	    	 var item = $("#buscarPorDocumentoPrestamo").val();
		     if($("#cedulaPrestamo").is(':checked')){
		    	 tipoDocumento = "cedula";
		     }else if($("#otroPrestamo").is(':checked')){
		    	 tipoDocumento = "otro";
		     }
		     var longitud = item.length;
		     if(longitud > 1){
			     $("#buscadorAgregarPrestamo").load("/prestamos/getInfoCliente",
					{
					   	'carpeta': carpeta,
					   	'tipoDocumento': tipoDocumento,
						'item': item
					},
				function(data){
					console.log("Buscar clientes");
					if($("#tipoDocumentoAcctPrestamo").val() == 'cedula'){
						$("#cedulaPrestamo").attr('checked', 'checked');
						$("#otroPrestamo").attr('checked', false);
					}else{
						$("#otroPrestamo").attr('checked', 'checked');
						$("#cedulaPrestamo").attr('checked', false);
					}
					if($("#msg").val()== "No se encontro el cliente"){
						 Swal.fire({
								title : 'Alerta!',
								text : 'No se encontro al cliente',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
							$("#cedulaPrestamo").attr('checked', 'checked');
							$("#otroPrestamo").attr('checked', false);
					}
					addEvents();
				});
		     }
	     }
	});
	
	$("#buscarPorNombrePrestamo").on("keyup", function(e) {
		e.preventDefault();
		e.stopImmediatePropagation();
	     if(e.which == 13){
	    	var item = $("#buscarPorNombrePrestamo").val();
	    	var carpeta = $("#carpetaIdPrestamo").val();
	    	var longitud = item.length;
		     if(longitud > 1){
			     $("#buscadorAgregarPrestamo").load("/prestamos/getInfoCliente",
					{
					   	'carpeta': carpeta,
					   	'tipoDocumento': '0',
						'item': item
					},
				function(data){
					console.log("Buscar clientes");
					if($("#tipoDocumentoAcctPrestamo").val() == 'cedula'){
						$("#cedulaPrestamo").attr('checked', 'checked');
						$("#otroPrestamo").attr('checked', false);
					}else{
						$("#otroPrestamo").attr('checked', 'checked');
						$("#cedulaPrestamo").attr('checked', false);
					}	
					if($("#msg").val()== "No se encontro el cliente"){
						 Swal.fire({
								title : 'Alerta!',
								text : 'No se encontro al cliente',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
						$("#cedulaPrestamo").attr('checked', 'checked');
						$("#otroPrestamo").attr('checked', false);	
					}
					addEvents();
				});
		     }
	     }
	});
	
	$("#tipo_prestamo").change(function(e){
		if($("#tipo_prestamo").val() == 2){
			ocultarDetalleAmortizacion();
			$("#divValorCuota").hide();
			$("#divPlazos").hide();
		}else{
			mostrarDetalleAmortizacion();
			$("#divValorCuota").show();
			$("#divPlazos").show();
		}
	});
	
	$("#guardarPrestamo").on("click", (function(e){
		 e.preventDefault();
		 e.stopImmediatePropagation();
		 var datos = $("#formulario_prestamo").serializeArray();
		 var idClienteTemp = $("#idClientePrestamo").val();
		 var idCarpetaTemp = $("#carpetaIdPrestamo").val();
		 var idCuentaTemp =  $("#id_cuenta").val(); 
		 var fecha = $("#fecha").val();
		 var cantidad_pagos = $("#cantidad_pagos").val();
		 var pagos = $("#pagos").val();
		 var tipoPrestamo = $("#tipo_prestamo").val();
		 
		 if(tipoPrestamo != 2 && (parseFloat(cantidad_pagos) > parseFloat(pagos))){
			 Swal.fire({
					title : 'Advertencia!',
					text : 'La cantidad de pagos no puede ser mayor al plazo',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
		 }else{
			 datos.push( {name:'idClienteTemp', value:idClienteTemp} );
			 datos.push( {name:'idCarpetaTemp', value:idCarpetaTemp} );
			 datos.push( {name:'idCuentaTemp', value:idCuentaTemp} ); 
			 datos.push( {name:'fechaTemp', value:fecha} );
			 $.post("/prestamos/guardar", datos,
				function(data){
					console.log("Guardar Prestamo");
					if(data == "0"){
						 Swal.fire({
								title : 'Advertencia!',
								text : 'No se genero el prestamo, El monto en el banco no puede ser menor al prestamo',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
					}else{
						if(data == "0"){
							 Swal.fire({
									title : 'Muy Bien!',
									text : 'Se genero el prestamo',
									position : 'top',
									icon : 'success',
									confirmButtonText : 'Cool'
								})
								$("#contenido").load("/clientes/buscarCliente",function(data){
									console.log("Lista de clientes");
									ocultarDetalleAmortizacion();
									addEvents();
								});	
						}
					}

			});
			return false;
			}
		 }
	));
	
	$("#aplicarCargos").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var idPrestamo = $("#prestamoAcct").val();
		 $.get("/prestamos/cuotasNoPagadas/"+idPrestamo,
			function(data){
			console.log("Cargar cuotas no pagadas");
			$("#selectCuotaCargo").replaceWith(data);
			$("#modalAplicarCargosCuotas").modal('show');
		});
	});
	
	$("#agregarCargoCuota").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var idPrestamo = $("#prestamoAcct").val();
		var motivo = $("#motivoCargoCuota").val();
		var monto = $("#montoCargoCuota").val();
		var cuota = $("#selectCuotaCargo").val();
		if(!cuota){
			cuota = 0;
		}
		
		 $.post("/prestamos/guardarCargoCuota/",{
			 "idPrestamo":idPrestamo,
			 "motivo":motivo,
			 "monto":monto,
			 "cuota":cuota
		 },function(data){
				console.log("Guardado de cargo");
				if(data == "1"){
					 Swal.fire({
							title : 'Muy bien!',
							text : 'Se ha guardado el cargo',
							position : 'top',
							icon : 'success',
							confirmButtonText : 'Cool'
						})
				}else{
					 Swal.fire({
							title : 'Alerta!',
							text : 'No se guardo el cargo',
							position : 'top',
							icon : 'warning',
							confirmButtonText : 'Cool'
						})
				}
				$("#motivoCargoCuota").val("");
				$("#montoCargoCuota").val("");
				$("#modalAplicarCargosCuotas").modal('hide');
				addEvents();
		 });
	});

/****************************************************** Fin Prestamos *******************************************************/
}


/****************************************************** Funciones ***********************************************************/

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
				}else{
					if($("#tipoDocumentoAcct").val() == 'cedula'){
						$("#cedula").attr('checked', 'checked');
						$("#otro").attr('checked', false);
					}else{
						$("#otro").attr('checked', 'checked');
						$("#cedula").attr('checked', false);
					}
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
	 var datos = $("#formulario_prestamo").serializeArray();
	 var fecha = $("#fecha").val();
	 datos.push( {name:'fechaTemp', value:fecha} );
	 $.post("/prestamos/amortizar",datos,
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
	
	//Interes
	if($("#tipo_prestamo").val()==2){
		var interes = monto * (parseFloat(tasa) / 100);
		$("#valor_interes").val(interes);
	}else{
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

function cargarDetallePrestamo(id){
	 $.get("/prestamos/detalleAmortizacion/"+id,
		function(data){
			console.log("Detalle Amortizacion");
			mostrarDetalleAmortizacion();
			$("#cuerpo_amortizacion").replaceWith(data);
			$("#prestamoAcct").val(id);
			$("#botonesAccionPrestamo").show();
			addEvents();
	 	});
}

function mostrarDetalleAmortizacion(){
	$("#cuerpoTabla").show();
}

function ocultarDetalleAmortizacion(){
	$("#cuerpoTabla").hide();
}
/******************************************************* Fin Funciones ******************************************************/
