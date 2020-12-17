// imprimirAbonoDetalle(2);
function addEvents(){
		
/************************************************** Configuraciones Iniciales **********************************************/
	
    $('[data-toggle="tooltip"]').tooltip();
    
	var clienteSeleccionado = $("#clienteSeleccionado").select2({
	    theme: 'bootstrap4',
	});
	
//	var productoSeleccionado = $("#productosSelectForCompra").select2({
//	    theme: 'bootstrap4',
//	});
	
	$(".select2").select2({
	    theme: 'bootstrap4',
	});
	
	if($("#tabla").length) {
		if( $.fn.DataTable.isDataTable('#tabla') == false){
			$('#tabla').DataTable({
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
	}
	
	$("#changeEmpresa").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		$("#modalCambioEmpresa").modal("show");
	});
	
	$("#btnCambioEmpresa").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var idEmpresa = $("#selectCambioEmpresa").val(); 
		$("#selectCambioEmpresa").load("/empresas/change",
			{
				'idEmpresa': idEmpresa,
			},
		function(data){
			location.reload();
		});
		$("#modalCambioEmpresa").modal("hide");
	});

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
		styleSeccionAll();
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
	
	//Lista clientes
	$("#listaClientes").click(function(e){
		$("#contenido").load("/clientes/",function(data){
			styleSeccionAll();
			console.log("Lista de clientes");
			addEvents();
		});	
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
		styleSeccionOriginal();
		$("#contenido").load("/clientes/buscarCliente",function(data){
			ocultarDetalleAmortizacion();
			console.log("Lista de clientes");
			addEvents();
		});
	});
	
	$("#moneda").change(function(e){
		var moneda = $("#moneda").val();
		$("#id_cuenta").load("/cuentas/listaPorMoneda",
		 {
			"moneda":moneda
		 },function(data){
			addEvents();
		});
	});
	
	$("#formImagenPrestamo").submit(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		
		var idPrestamo = $("#prestamoAcct").val();
		var fotos = $("#inputFotosPrestamos").val();
		var formData = new FormData(this);
		
		if(!fotos){
			$("#cargarImagenesPrestamoModal").modal("hide");
			Swal.fire({
				  title: 'Alerta!',
				  text: "Debe seleccionar al menos una imagen",
				  icon: 'warning',
				  position : 'top',
				  showCancelButton: false,
				  confirmButtonColor: '#3085d6',
				  confirmButtonText: 'Ok!'
				}).then((result) => {
				  if (result.isConfirmed) {
					  $("#cargarImagenesPrestamoModal").modal("show");
				  }
				})
		}else{
			  $.ajax({
		          url: "/prestamos/cargarImagenes",
		          type: "POST",
		          data: formData,
		          enctype: 'multipart/form-data',
		          processData: false,
		          contentType: false,
		          cache: false,
		          success: function (res) {
			        addEvents();
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
		        }});
		}
	});
	
	$("#btnCargarImagenesPrestamo").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var idPrestamo = $("#prestamoAcct").val();
		$("#cargarImagenesPrestamoModal").modal("show");
//		$("#contenido").load("/clientes/buscarCliente",function(data){
//			ocultarDetalleAmortizacion();
//			console.log("Lista de clientes");
//			addEvents();
//		});
	});

	
	$("#btnAddDataVehiculo").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var idPrestamo = $("#prestamoAcct").val();
		alert("id prestamo: "+idPrestamo);
//		$("#contenido").load("/clientes/buscarCliente",function(data){
//			ocultarDetalleAmortizacion();
//			console.log("Lista de clientes");
//			addEvents();
//		});
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
		ocultarDetalleAmortizacion();
		styleSeccionAll();
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
			var moneda = $("#moneda").val();
			$("#id_cuenta").load("/cuentas/listaPorMoneda",
			 {
				"moneda":moneda
			 },function(data){
				addEvents();
			});
		});
	});
	
	$("#cancelarFormPrestamo").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		styleSeccionOriginal();
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
	
	$("#descuentos").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		$("#selectCuotasMora").show();
		$('#tipoDescuento option:nth-child(1)').prop("selected", true).change(); 
		var idPrestamo = $("#prestamoAcct").val();
		var tipoDescuento = $("#tipoDescuento").val();
		 $.get("/prestamos/cuotasNoPagadasDescuentosTemp/"+idPrestamo+"/"+tipoDescuento,
			function(data){
				console.log("Cargar cuotas no pagadas");
				$("#selectCuotasMora").replaceWith(data);
				$("#modalDescuentos").modal('show');
		});
	});
	
	$("#tipoDescuento").change(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var idPrestamo = $("#prestamoAcct").val();
		var tipoDescuento = $("#tipoDescuento").val();
		var tipoPrestamo = $("#tipoPrestamo").val()
		if(tipoDescuento == 1 && tipoPrestamo == 1){
			 $.get("/prestamos/cuotasNoPagadasDescuentosTemp/"+idPrestamo+"/"+tipoDescuento,
						function(data){
						$("#selectCuotasMora").replaceWith(data);
					});
		}else{
			 $.get("/prestamos/cuotasNoPagadasDescuentos/"+idPrestamo+"/"+tipoDescuento,
						function(data){
						$("#selectCuotasMora").replaceWith(data);
					});
		}
	});
	
	$("#agregaDescuento").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var idPrestamo = $("#prestamoAcct").val();
		var montoDescuento = $("#montoDescuento").val();
		var tipoDescuento = $("#tipoDescuento").val();
		var tipoPrestamo = $("#tipoPrestamo").val()
		var cuota = $("#selectCuotasMora").val();
		
		if(!montoDescuento){
			$("#modalDescuentos").modal('hide');
			Swal.fire({
				title : 'Alerta!',
				text : 'Debe ingresar un monto',
				position : 'top',
				icon : 'warning',
				confirmButtonText : 'Cool'
			})
		}else{
			if(tipoPrestamo == "2" && !cuota){
				$("#modalDescuentos").modal('hide');
				Swal.fire({
					title : 'Alerta!',
					text : 'No hay datos para aplicar el descuento',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}else{	
				//Validamos que el descuento no sea mayor que el monto generado
				 $.post("/prestamos/validarDescuento/",
				 {
					 "idPrestamo":idPrestamo,
					 "monto":montoDescuento,
					 "tipo":tipoDescuento,
					 "cuota":cuota
				 },function(data){
					 if(data == 1){
						 $.post("/prestamos/guardarDescuento/",{
							 "idPrestamo":idPrestamo,
							 "monto":montoDescuento,
							 "tipo":tipoDescuento,
							 "cuota":cuota
						 },function(data){
								console.log("Guardado de descuento");
								if(data == "1"){
									 Swal.fire({
											title : 'Muy bien!',
											text : 'Se ha guardado el descuento',
											position : 'top',
											icon : 'success',
											confirmButtonText : 'Cool'
										})
								}else{
									 Swal.fire({
											title : 'Alerta!',
											text : 'No se guardo el descuento',
											position : 'top',
											icon : 'warning',
											confirmButtonText : 'Cool'
										})
								}
								$("#montoDescuento").val("");
								$("#modalDescuentos").modal('hide');
								addEvents();
								cargarDetallePrestamo(idPrestamo);
						 });	
					 }else{
						 $("#modalDescuentos").modal('hide');
						 Swal.fire({
								title : 'Alerta!',
								text : 'El descuento no puede ser mayor al balance',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
						})
					 }
				});
			}
		}
	});
	
	$("#agregarCargoCuota").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var idPrestamo = $("#prestamoAcct").val();
		var motivo = $("#motivoCargoCuota").val();
		var monto = $("#montoCargoCuota").val();
		var cuota = $("#selectCuotaCargo").val();
		var nota = $("#notaCargoCuota").val();
		if(!cuota){
			cuota = 0;
		}
		
		if(!monto){
			$("#modalAplicarCargosCuotas").modal('hide');
			Swal.fire({
				title : 'Alerta!',
				text : 'Debe ingresar un monto',
				position : 'top',
				icon : 'warning',
				confirmButtonText : 'Cool'
			})
		}else{
			 $.post("/prestamos/guardarCargoCuota/",{
				 "idPrestamo":idPrestamo,
				 "motivo":motivo,
				 "monto":monto,
				 "cuota":cuota,
				 "nota":nota
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
//					$("#motivoCargoCuota").val("");
					$("#montoCargoCuota").val("");
					$("#modalAplicarCargosCuotas").modal('hide');
					addEvents();
					cargarDetallePrestamo(idPrestamo);
			 });
		}
	});
	
	$("#recibirAbono").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var tipoPrestamoAcct = $("#tipoPrestamo").val();
		if(tipoPrestamoAcct == 2){
			$("#optionCuotasCapital").hide();
		}else{
			$("#optionCuotasCapital").show();
		}
		var idPrestamoAcct = $("#prestamoAcct").val();
		
		if($("#tablaPagos").length){
			$("#tablaPagos").load("/prestamos/cargarPagoTemp/"+idPrestamoAcct,function(data){
				addEvents();
				montoTotalAbono();
				$("#montoAbonoCuota").val("");
			});
		}
		
		$("#modalRecibirAbonoCuotas").modal('show');
	});
	
	$("#agregarAbonoCuota").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var idPrestamo = $("#prestamoAcct").val();
		var monto = $("#montoAbono").val();
		var tipoCuota = $("#selectTipoCuotaAbono").val();
		var balancePendiente = parseFloat($("#capitalPendienteTemp").val());
		
		var totalesCuota = parseFloat($("#totalesCuotas").text());
		
		if(monto > totalesCuota){
			Swal.fire({
				title : 'Alerta!',
				text : 'El monto no puede ser mayor al total',
				position : 'top',
				icon : 'warning',
				confirmButtonText : 'Cool'
			})
			$("#modalRecibirAbonoCuotas").modal('hide');
		}else{
			if(!monto || monto<1){
				$("#modalRecibirAbonoCuotas").modal('hide');
				Swal.fire({
					title : 'Alerta!',
					text : 'Debe ingresar un monto',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}else{
//				if(monto>balancePendiente){
//					$("#modalRecibirAbonoCuotas").modal('hide');
//					Swal.fire({
//						title : 'Alerta!',
//						text : 'El monto a pagar no puede ser mayor que el balance pendiente',
//						position : 'top',
//						icon : 'warning',
//						confirmButtonText : 'Cool'
//					})
//				}else{
					 $.post("/prestamos/guardarAbonoCuota/",{
						 "idPrestamo":idPrestamo,
						 "monto":monto,
						 "tipoCuota":tipoCuota
					 },function(data){
							console.log("Guardado de cargo");
							if(data > 0){
								 Swal.fire({
										title : 'Muy bien!',
										text : 'Se ha guardado el abono',
										position : 'top',
										icon : 'success',
										confirmButtonText : 'Cool'
									})
								imprimirAbonoDetalle(data);	
							}else{
								 Swal.fire({
										title : 'Alerta!',
										text : 'No se guardo el abono',
										position : 'top',
										icon : 'warning',
										confirmButtonText : 'Cool'
									})
							}
							$("#archivoPago").val("");
							$("#montoAbonoCuota").val("");
							$("#modalRecibirAbonoCuotas").modal('hide');
							addEvents();
							cargarDetallePrestamo(idPrestamo);
					 });
//				}
			}
		}
		
	});

	$("#eliminarPagosTemp").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var id = $("#prestamoAcct").val();
		 $.get("/prestamos/eliminarPagosTemp/"+id,
			function(data){
			 $("#archivoPago").val("");
			 $("#montoAbonoCuota").val("");
			 $("#modalRecibirAbonoCuotas").modal("hide");
			 	addEvents();
		 	});
	});
	
	$("#crearNota").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var prestamoAcct = $("#prestamoAcct").val();
		//Cargamos el combo de cuotas
		$("#cuotaNota").load("/prestamos/mostrarCotasPrestamo/"+prestamoAcct,function(data){
			console.log("Cuotas");
			addEvents();
		});
		$("#modalAgregarNota").modal('show');
	});
	
	$("#btnAgregarNota").click(function(e){
		e.preventDefault();
		e.stopImmediatePropagation();
		var fecha = $("#fechaNota").val();
		var nota = $("#valorNota").val();
		var cuota = $("#cuotaNota").val(); 
		var prestamoAcct = $("#prestamoAcct").val();

		 $.post("/notas/guardarNota",{
			 "idPrestamo":prestamoAcct,
			 "nota":nota,
			 "fecha":fecha,
			 "cuota":cuota
		 },function(data){
				console.log("Guardado de Nota");
				if(data == "1"){
					 Swal.fire({
							title : 'Muy bien!',
							text : 'Se ha guardado la nota',
							position : 'top',
							icon : 'success',
							confirmButtonText : 'Cool'
						})
				}else{
					 Swal.fire({
							title : 'Alerta!',
							text : 'No se guardo la nota',
							position : 'top',
							icon : 'warning',
							confirmButtonText : 'Cool'
						})
				}
				$("#modalAgregarNota").modal('hide');
				$("#valorNota").val("");
				verNotas(prestamoAcct);
		 });
		
	});
/****************************************************** Fin Prestamos *******************************************************/

/******************************************************** Caja **************************************************************/

$("#caja").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	styleSeccionAll();
	$("#contenido").load("/cajas/mostrarCuadre",function(data){
		console.log("Cuadre de Caja");
		addEvents();
	});
});

$("#imprimirCuadre").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var fecha = $("#fechaCuadreCaja").val();
	var user = $("#usuarioCuadreCaja").val();
	
	if(!fecha){
		fecha = "";
	}
	
	$.post("/reportes/cuadreCaja",{
	 "fecha" : fecha,
	 "userId" : user
	},function(data){
		impresion(data);
		addEvents();
	});
});

$("#btnCarpetaCajas").click(function(e){
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
		            url: "/carpetas/buscarCarpetaCajas/"+carpeta,
		            type: "GET",
//		            data: "",
//		            enctype: 'multipart/form-data',
		            processData: false,
		            contentType: false,
		            cache: false,
		            success: function (res) {
						$("#buscarCarpeta").replaceWith(res);
		            	var msg = $("#msgId").val();
			            if(msg=="0"){
							Swal.fire({
								title : 'No tienes carpetas creadas!',
//								text : 'No tienes carpetas creadas',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
							addEvents();
						}else{
							Swal.fire({
								title : 'Muy bien!',
								text : 'Carpeta Seleccionada',
								position : 'top',
								icon : 'success',
								confirmButtonText : 'Cool'
							})
							
							var fecha = $("#fechaCuadreCaja").val();
							var user = $("#usuarioCuadreCaja").val();
							$("#contenido").load("/cajas/mostrarCuadre",
								{
									"fecha" : fecha,
									"userId" : user
								},function(data){
								var userAcctCaja = $("#userAcctCaja").val();	
								$("#usuarioCuadreCaja option[value="+ userAcctCaja +"]").attr("selected",true);
								console.log("Cuadre de Caja");
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

$("#btnExitCarpetaCaja").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	  $.ajax({
          url: "/carpetas/buscarCarpetaCajas",
          type: "GET",
//          data: "",
//          enctype: 'multipart/form-data',
          processData: false,
          contentType: false,
          cache: false,
          success: function (res) {
				$("#buscarCarpeta").replaceWith(res);
          	var msg = $("#msgId").val();
	            if(msg=="0"){
					addEvents();
					location.href = '/logout';
				}else{
					var fecha = $("#fechaCuadreCaja").val();
					var user = $("#usuarioCuadreCaja").val();
					$("#contenido").load("/cajas/mostrarCuadre",
						{
							"fecha" : fecha,
							"userId" : user
						},function(data){
						var userAcctCaja = $("#userAcctCaja").val();	
						$("#usuarioCuadreCaja option[value="+ userAcctCaja +"]").attr("selected",true);
						console.log("Cuadre de Caja");
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
});	

$("#fechaCuadreCaja").change(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	var fecha = $("#fechaCuadreCaja").val();
	var user = $("#usuarioCuadreCaja").val();
	$("#contenido").load("/cajas/mostrarCuadre",
		{
			"fecha" : fecha,
			"userId" : user
		},function(data){
		var userAcctCaja = $("#userAcctCaja").val();	
		$("#usuarioCuadreCaja option[value="+ userAcctCaja +"]").attr("selected",true);
		console.log("Cuadre de Caja");
		addEvents();
	});
});

$("#usuarioCuadreCaja").change(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	var fecha = $("#fechaCuadreCaja").val();
	var user = $("#usuarioCuadreCaja").val();
	$("#contenido").load("/cajas/mostrarCuadre",
		{
			"fecha" : fecha,
			"userId" : user
		},function(data){
		var userAcctCaja = $("#userAcctCaja").val();	
		$("#usuarioCuadreCaja option[value="+ userAcctCaja +"]").attr("selected",true);
		console.log("Cuadre de Caja");
		addEvents();
	});
});

$("#btnDepositar").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	$("#modalDepositar").modal("show");
});

$("#depositar").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var idCuenta = $("#idCuenta").val();
	var montoBanco = $("#montoBanco").val();
	
	if(montoBanco){
		$("#modalDepositar").modal("hide");
		 $.post("/cuentas/depositar",{
			 "idCuenta":idCuenta,
			 "montoBanco":montoBanco
		 },function(data){
			 if(data == "1"){
				 Swal.fire({
						title : 'Muy bien!',
						text : 'Deposito guardado correctamente',
						position : 'top',
						icon : 'success',
						confirmButtonText : 'Cool'
					})
				$("#montoBanco").val("");
			 }else{
				 Swal.fire({
						title : 'Error!',
						text : 'No se pudo guardar el deposito',
						position : 'top',
						icon : 'error',
						confirmButtonText : 'Cool'
					})
			 }
		 });
	}else{
		$("#modalDepositar").modal("hide");
		Swal.fire({
			  title: 'Alerta!',
			  text: "El monto no puede estar vacio",
			  icon: 'warning',
			  position : 'top',
			  showCancelButton: false,
			  confirmButtonColor: '#3085d6',
			  confirmButtonText: 'Ok!'
			}).then((result) => {
			  if (result.isConfirmed) {
				  $("#modalDepositar").modal("show");
			  }
			})
	}
});

/****************************************************** Fin Caja **********************************************************/

/****************************************************** Contabilidad **************************************************************/

$("#contabilidad").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	styleSeccionAll();
	$("#contenido").load("/contabilidad/mostrarContabilidad",function(data){
		console.log("Contabilidad");
		addEvents();
	});
});

$("#tarjetaEnlaces").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#contenido").load("/contabilidad/enlaces",function(data){
		console.log("Enlaces");
		addEvents();
	});
});

$("#tarjetaCuentasXPagar").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#contenido").load("/contabilidad/cuentasXPagar",function(data){
		console.log("Cuentas x Pagar");
		addEvents();
	});
});

$("#tarjetaCuentasXCobrar").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#contenido").load("/contabilidad/cuentasXCobrar",function(data){
		console.log("Cuentas x Cobrar");
		addEvents();
	});
});

$("#tarjetaCompras").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#contenido").load("/contabilidad/compras",function(data){
		console.log("Compras");
		addEvents();
	});
});

$("#tarjetaCuentaContables").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#contenido").load("/cuentasContables/listaCuentasContables",function(data){
		console.log("Cuentas contables");
		addEvents();
	});
});

$("#tarjetaInventarios").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	
	$("#contenido").load("/productos/listaProductos",function(data){
		console.log("lista articulos");
		$("#modalProductos").modal("show");
		addEvents();
	});
});

$("#itbisPorPagar").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	
	$("#contenido").load("/contabilidad/listaItbisXPagar",function(data){
		addEvents();
	});
});

$("#comprasPorPagar").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	
	$("#contenido").load("/contabilidad/cuentasXPagar",function(data){
		console.log("Cuentas x Pagar");
		addEvents();
	});
});

$("#verificarCuenta").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	
//	$("#contenido").load("/productos/listaProductos",function(data){
//		console.log("lista articulos");
		$("#modalVerificarCuenta").modal("show");
		addEvents();
//	});
});

$("#agregarCuentaTempSuplidor").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var id = $("#cuentaContableSuplidor").val();
	$("#tablaCuentasTempSuplidor").load("/contabilidad/guardarCuentaTempSuplidor/"+id,function(data){
		addEvents();
	});
});

$("#agregarEnlace").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var cuentaContable = $("#cuentaContableAuxiliar").val();
	if(cuentaContable!=""){
		$.post("/contabilidad/agregarEnlace",
			{
				"cuentaContableId":cuentaContable,
				"identificador": "formaPago"
			},function(data){
				if(data == "1"){
					$("#tablaEnlaces").load("/contabilidad/mostrarEnlaces",function(data){
						addEvents();
					});
				}else if(data == "2"){
					Swal.fire({
						title : 'Advertencia!',
						text : 'El enlace ya existe',
						position : 'top',
						icon : 'warning',
						confirmButtonText : 'Cool'
					})
					$("#tablaEnlaces").load("/contabilidad/mostrarEnlaces",function(data){
						addEvents();
					});
				}else{
					Swal.fire({
						title : 'Error!',
						text : 'No se pudo crear el enlace',
						position : 'top',
						icon : 'error',
						confirmButtonText : 'Cool'
					})
				}
			});
	}else{
		Swal.fire({
			title : 'Error!',
			text : 'Debe seleccionar una cuenta contable',
			position : 'top',
			icon : 'error',
			confirmButtonText : 'Cool'
		})
	}
});

$("#agregarEnlaceItbis").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var cuentaContable = $("#cuentaContableAuxiliarItbis").val();
	var impuesto = $("#impuestoEnlace").val();
	
	if(!impuesto || impuesto < 0){
		Swal.fire({
			title : 'Advertencia!',
			text : 'Debe ingresar el impuesto',
			position : 'top',
			icon : 'warning',
			confirmButtonText : 'Cool'
		})
	}else{
		if(cuentaContable!=""){
			$.post("/contabilidad/agregarEnlace",
				{
					"cuentaContableId":cuentaContable,
					"identificador": "itbis",
					"impuesto":impuesto
				},function(data){
					if(data == "1"){
						$("#tablaEnlacesItbis").load("/contabilidad/mostrarEnlacesItbis",function(data){
							addEvents();
						});
					}else if(data == "2"){
						Swal.fire({
							title : 'Advertencia!',
							text : 'El enlace ya existe',
							position : 'top',
							icon : 'warning',
							confirmButtonText : 'Cool'
						})
						$("#tablaEnlacesItbis").load("/contabilidad/mostrarEnlacesItbis",function(data){
							addEvents();
						});
					}else{
						Swal.fire({
							title : 'Error!',
							text : 'No se pudo crear el enlace',
							position : 'top',
							icon : 'error',
							confirmButtonText : 'Cool'
						})
					}
				});
		}else{
			Swal.fire({
				title : 'Error!',
				text : 'Debe seleccionar una cuenta contable',
				position : 'top',
				icon : 'error',
				confirmButtonText : 'Cool'
			})
		}
	}
});

$("#agregarEnlaceProcesos").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var cuentaContable = $("#cuentaContableAuxiliarProcesos").val();
	if(cuentaContable!=""){
		$.post("/contabilidad/agregarEnlace",
			{
				"cuentaContableId":cuentaContable,
				"identificador": "procesos"
			},function(data){
				if(data == "1"){
					$("#tablaEnlacesProcesos").load("/contabilidad/mostrarEnlacesProcesos",function(data){
						addEvents();
					});
				}else if(data == "2"){
					Swal.fire({
						title : 'Advertencia!',
						text : 'El enlace ya existe',
						position : 'top',
						icon : 'warning',
						confirmButtonText : 'Cool'
					})
					$("#tablaEnlacesProcesos").load("/contabilidad/mostrarEnlacesProcesos",function(data){
						addEvents();
					});
				}else{
					Swal.fire({
						title : 'Error!',
						text : 'No se pudo crear el enlace',
						position : 'top',
						icon : 'error',
						confirmButtonText : 'Cool'
					})
				}
			});
	}else{
		Swal.fire({
			title : 'Error!',
			text : 'Debe seleccionar una cuenta contable',
			position : 'top',
			icon : 'error',
			confirmButtonText : 'Cool'
		})
	}
});

$("#agregarEnlaceRetencion").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var cuentaContable = $("#cuentaContableAuxiliarRetencion").val();
	if(cuentaContable!=""){
		$.post("/contabilidad/agregarEnlace",
			{
				"cuentaContableId":cuentaContable,
				"identificador": "retencion"
			},function(data){
				if(data == "1"){
					$("#tablaEnlacesRetencion").load("/contabilidad/mostrarEnlacesRetencion",function(data){
						addEvents();
					});
				}else if(data == "2"){
					Swal.fire({
						title : 'Advertencia!',
						text : 'El enlace ya existe',
						position : 'top',
						icon : 'warning',
						confirmButtonText : 'Cool'
					})
					$("#tablaEnlacesProcesos").load("/contabilidad/mostrarEnlacesRetencion",function(data){
						addEvents();
					});
				}else{
					Swal.fire({
						title : 'Error!',
						text : 'No se pudo crear el enlace',
						position : 'top',
						icon : 'error',
						confirmButtonText : 'Cool'
					})
				}
			});
	}else{
		Swal.fire({
			title : 'Error!',
			text : 'Debe seleccionar una cuenta contable',
			position : 'top',
			icon : 'error',
			confirmButtonText : 'Cool'
		})
	}
});

$("#agregarEnlaceEntidadEnlace").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var cuentaContable = $("#cuentaContableAuxiliarEntidadEnlace").val();
	if(cuentaContable!=""){
		$.post("/contabilidad/agregarEnlace",
			{
				"cuentaContableId":cuentaContable,
				"identificador": "entidadEnlace"
			},function(data){
				if(data == "1"){
					$("#tablaEnlacesEntidadEnlace").load("/contabilidad/mostrarEnlacesEntidadEnlace",function(data){
						addEvents();
					});
				}else if(data == "2"){
					Swal.fire({
						title : 'Advertencia!',
						text : 'El enlace ya existe',
						position : 'top',
						icon : 'warning',
						confirmButtonText : 'Cool'
					})
					$("#tablaEnlacesEntidadEnlace").load("/contabilidad/mostrarEnlacesEntidadEnlace",function(data){
						addEvents();
					});
				}else{
					Swal.fire({
						title : 'Error!',
						text : 'No se pudo crear el enlace',
						position : 'top',
						icon : 'error',
						confirmButtonText : 'Cool'
					})
				}
			});
	}else{
		Swal.fire({
			title : 'Error!',
			text : 'Debe seleccionar una cuenta contable',
			position : 'top',
			icon : 'error',
			confirmButtonText : 'Cool'
		})
	}
});

$("#agregarPagoCompraTemp").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var id = $("#idCompraCxP").val();
	var fecha = $("#fechaPagoCompra").val();
	var monto = $("#montoPagoCompra").val();
	var cuentaContableId = $("#cuentaContableEnlace").val();
	var referencia = $("#referenciaPagoCompra").val();
	
	if((!monto || monto<1) || !fecha || cuentaContableId=="" || !referencia){
		$("#modalPagoCompra").modal('hide');
			Swal.fire({
			title: 'Alerta!',
			text: "Todos los campos son requeridos",
			icon: 'warning',
			position : 'top',
			showCancelButton: false,
			 confirmButtonColor: '#3085d6',
			  confirmButtonText: 'Ok!'
			}).then((result) => {
			  if (result.isConfirmed) {
				$("#modalPagoCompra").modal("show");
			  }
			});
	}else{
		 $.post("/compras/guardarPagoTemp",
				 {
				  	"id":id,
				  	"fecha":fecha,
					"monto":monto,
					"cuentaContableId":cuentaContableId,
					"referencia":referencia
				},function(response){
					if(response == 1){
						$("#fechaPagoCompra").val("");
						$("#montoPagoCompra").val("");
						$("#referenciaPagoCompra").val("");
						$("#cuentaContableEnlace option[value='']").attr("selected",true);
						$("#tablaPagosCompraTemp").load("/compras/pagosTemporales/"+id,function(data){
							addEvents();
						});
					}else{
						Swal.fire({
							title : 'Alerta!',
							text : 'No se pudo crear el registro',
							position : 'top',
							icon : 'warning',
							confirmButtonText : 'OK!'
						}) 
					}
				});
	}
});

$("#prestamosPorPagar").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#contenido").load("/prestamos/prestamosPorPagar",function(data){
		console.log("Prestamos por pagar");
		addEvents();
	});
});

$("#guardarPrestamoEntidad").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var idInversionista = $("#inversionistaPrestamoEntidad").val();
	var idCuentaContableEntidad = $("#cuentaContablePrestamoEntidad").val();
	var monto = $("#montoPrestamoEntidad").val();
	var tasa = $("#tasaPrestamoEntidad").val();
	var idCuentaContableFormaPago = $("#cuentaContableFormaPago").val();
	var fecha = $("#fechaPrestamoEntidad").val();
	
	if(idCuentaContableEntidad == "" || idCuentaContableFormaPago == "" || idInversionista == "" || !fecha){
		$("#modalPrestamoEntidad").modal('hide');
		Swal.fire({
		title: 'Alerta!',
		text: "Todos los campos son requeridos",
		icon: 'warning',
		position : 'top',
		showCancelButton: false,
		 confirmButtonColor: '#3085d6',
		  confirmButtonText: 'Ok!'
		}).then((result) => {
		  if (result.isConfirmed) {
			$("#modalPrestamoEntidad").modal("show");
		  }
		});
	}else{
		if((!monto || monto<0) || (!tasa || tasa<0)){
			$("#modalPrestamoEntidad").modal('hide');
			Swal.fire({
			title: 'Alerta!',
			text: "Los montos deben ser valores numericos positivos",
			icon: 'warning',
			position : 'top',
			showCancelButton: false,
			 confirmButtonColor: '#3085d6',
			  confirmButtonText: 'Ok!'
			}).then((result) => {
			  if (result.isConfirmed) {
				$("#modalPrestamoEntidad").modal("show");
			  }
			});
		}else{
			$.post("/prestamos/guardarPrestamoEntidad",
			{
				"idInversionista":idInversionista,
				"idCuentaContableEntidad":idCuentaContableEntidad,
				"monto":monto,
				"tasa":tasa,
				"idCuentaContableFormaPago":idCuentaContableFormaPago,
				"fecha":fecha
			},function(response){
				if(response == 1){
					$("#modalPrestamoEntidad").modal('hide');
					Swal.fire({
					title: 'Muy bien!',
					text: "Registro guardado",
					icon: 'success',
					position : 'top',
					showCancelButton: false,
					 confirmButtonColor: '#3085d6',
					  confirmButtonText: 'Ok!'
					}).then((result) => {
					  if (result.isConfirmed) {
						 $("#inversionistaPrestamoEntidad option[value='']").attr("selected",true);
						 $("#cuentaContablePrestamoEntidad option[value='']").attr("selected",true);
						 $("#cuentaContableFormaPago option[value='']").attr("selected",true);
						 $("#montoPrestamoEntidad").val("");
						 $("#tasaPrestamoEntidad").val("");
						$("#modalPrestamoEntidad").modal("show");
						$("#tablaPrestamosEntidades").load("/prestamos/actualizarListaPrestamosEntidades",function(data){
							addEvents();
						});
					  }
					});
				}else{
					$("#modalPrestamoEntidad").modal('hide');
					Swal.fire({
					title: 'Alerta!',
					text: "No se pudo guardar el registro",
					icon: 'warning',
					position : 'top',
					showCancelButton: false,
					 confirmButtonColor: '#3085d6',
					  confirmButtonText: 'Ok!'
					}).then((result) => {
					  if (result.isConfirmed) {
						$("#modalPrestamoEntidad").modal("show");
					  }
					});
				}
			});
		}
	}
});

$("#agregarInversionista").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#modalInversionista").modal("show");
});

$("#guardarInversionista").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var nombre = $("#nombreInversionista").val();
	var telefono = $("#telefonoInversionista").val();
	var direccion = $("#direccionInversionista").val();
	
	if(!nombre){
		$("#modalInversionista").modal("hide");
		 Swal.fire({
			  title: 'Alerta!',
			  text: "El nombre es requerido",
			  icon: 'warning',
			  position : 'top',
			  showCancelButton: false,
			  confirmButtonColor: '#3085d6',
			  confirmButtonText: 'Ok!'
			}).then((result) => {
			  if (result.isConfirmed) {
	             $("#modalInversionista").modal("show");
			  }
			})
	}else{
		$.post("/contabilidad/agregarInversionista",{
			"nombre": nombre,
			"telefono": telefono,
			"direccion": direccion
		},function(data){
			if(data == 1){
				$("#modalInversionista").modal("hide");
				 Swal.fire({
					  title: 'Muy bien!',
					  text: "Registro guardado",
					  icon: 'success',
					  position : 'top',
					  showCancelButton: false,
					  confirmButtonColor: '#3085d6',
					  confirmButtonText: 'Ok!'
					}).then((result) => {
					  if (result.isConfirmed) {
						 $("#nombreInversionista").val("");
						 $("#telefonoInversionista").val("");
						 $("#direccionInversionista").val("");
			             $("#modalInversionista").modal("show");
					  }
					})
			}else{
				$("#modalInversionista").modal("hide");
				 Swal.fire({
					  title: 'Alerta!',
					  text: "No se pudo guardar el registro",
					  icon: 'warning',
					  position : 'top',
					  showCancelButton: false,
					  confirmButtonColor: '#3085d6',
					  confirmButtonText: 'Ok!'
					}).then((result) => {
					  if (result.isConfirmed) {
			             $("#modalInversionista").modal("show");
					  }
					})
			}
		});
	}
});

$("#agregarPrestamoEntidad").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#modalPrestamoEntidad").modal("show");
});

$("#agregarPagosCompra").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var id = $("#idCompraCxP").val();
	var balance = $("#balanceCompra").val();
	var balancePagos = $("#totalBalancePagoCompra").text(); 
	if(parseFloat(balancePagos) > parseFloat(balance)){
		$("#modalPagoCompra").modal("hide");
		Swal.fire({
			  title: 'Alerta!',
			  text: "El monto total del abono no puede ser mayor al balance",
			  icon: 'warning',
			  position : 'top',
			  showCancelButton: false,
			  confirmButtonColor: '#3085d6',
			  confirmButtonText: 'Ok!'
			}).then((result) => {
			  if (result.isConfirmed) {
				  $("#modalPagoCompra").modal("show");
			  }
			})
	}else{
		$.post("/compras/guardarPagos",
			{
				"id":id
			},function(data){
				if(data == 1){
					$("#modalPagoCompra").modal("hide");
					Swal.fire({
						title : 'Muy bien!',
						text : 'Pagos creados correctamente',
						position : 'top',
						icon : 'success',
						confirmButtonText : 'OK!'
					}) 
					$("#idCompraCxP").val("");
					$("#fechaInfoCompra").val("");
					$("#suplidorInfoCompra").val("");
					$("#facturaInfoCompra").val("");
					$("#comprobanteInfoCompra").val("");
					$("#totalInfoCompra").val("");
					$("#retencionInfoCompra").val("");
					$("#abonoInfoCompra").val("");
					$("#balanceInfoCompra").val("");
					$("#tablaCompras").load("/compras/listaCompras",function(data){
						$("#tablaVerificarCuenta").load("/compras/actualizarTablaVerificarCuenta",function(data){
							addEvents();
						});
					});
				}else{
					$("#modalPagoCompra").modal("hide");
					Swal.fire({
						  title: 'Alerta!',
						  text: "No se pudo guardar los pagos",
						  icon: 'warning',
						  position : 'top',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  confirmButtonText: 'Ok!'
						}).then((result) => {
						  if (result.isConfirmed) {
							  $("#modalPagoCompra").modal("show");
						  }
						})
				}
			});
	}
});

$("#crearPago").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var idCompra = $("#idCompraCxP").val();
	if(idCompra != ""){
		var balance = $("#balanceInfoCompra").val();
		$("#balanceCompra").val(balance);
		$("#tablaPagosCompraTemp").load("/compras/pagosTemporales/"+idCompra,function(data){
			$("#modalPagoCompra").modal("show");
			addEvents();
		});
	}else{
		Swal.fire({
			title : 'Alerta!',
			text : 'Debe seleccionar una compra',
			position : 'top',
			icon : 'warning',
			confirmButtonText : 'OK!'
		}) 
	}
});

$("#reporteCompraPago").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#contenido").load("/compras/reportePagosCompra",function(data){
		addEvents();
	});
});

$("#agregarProductoCompra").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#modalAgregarProductosCompras").modal("show");
});

$("#regresarAContabilidad").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	styleSeccionAll();
	$("#contenido").load("/contabilidad/mostrarContabilidad",function(data){
		console.log("Contabilidad");
		addEvents();
	});
});

$("input[name='tipoProductoRadio']").change(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	 if($("#inventarioRadio").is(':checked')){
		$("#productosSelectForCompra").load("/productos/reloadProductsCompra/"+1,function(data){
			addEvents();
		});
    }else if($("#servicioRadio").is(':checked')){
    	$("#productosSelectForCompra").load("/productos/reloadProductsCompra/"+23,function(data){
			addEvents();
		});
    }
});

$("#ingresarProducto").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var id = $("#idSelectedProduct").val();
	if(!id || id == ""){
		Swal.fire({
			title : 'Error!',
			text : 'Debe seleccionar un producto',
			position : 'top',
			icon : 'error',
			confirmButtonText : 'Cool'
		})
	}else{
		
		Swal.fire({
			  title: 'Cantidad a ingresar',
			  input: 'number',
			  position : 'top',
			  inputAttributes: {
			    autocapitalize: 'off'
			  },
			  showCancelButton: true,
			  confirmButtonText: 'Ingresar',
			  cancelButtonColor: '#F44336',
			  cancelButtonText: 'Cancelar',
			  showLoaderOnConfirm: true,
			  preConfirm: (cantidad) => {
				  if(cantidad<0 || !cantidad){
						Swal.fire({
							title : 'Error!',
							text : 'La cantidad debe ser mayor a 0',
							position : 'top',
							icon : 'error',
							confirmButtonText : 'OK!'
						})
				  }else{
					  $.post("/productos/modificarCantidad",
						 {
						  	"id":id,
						  	"tipo":"entrada",
							"cantidad":cantidad
						},function(data){
							Swal.fire({
								title : 'Muy bien!',
								text : 'Cantidad actualizada',
								position : 'top',
								icon : 'success',
								confirmButtonText : 'OK!'
							}) 
							  $("#tablaProductos").load("/productos/listaProductosFragment",function(data){
					            	 console.log("lista productos");
					            	 $("#nombreInfoPoduct").val("");
					            	 $("#costoInfoProduct").val(""); 
					            	 $("#precioVentaInfoProduct").val("");
					            	 $("#cantidadInfoProduct").val(""); 
					            	 $("#tipoActivoInfoProduct").val("");
					            	 $("#idSelectedProduct").val("");
							    	 addEvents();
					             });
						});
				  }
			  }
			});
	}
});

$("#sacarProducto").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var id = $("#idSelectedProduct").val();
	if(!id || id == ""){
		Swal.fire({
			title : 'Error!',
			text : 'Debe seleccionar un producto',
			position : 'top',
			icon : 'error',
			confirmButtonText : 'Cool'
		})
	}else{
		
		Swal.fire({
			  title: 'Cantidad a retirar',
			  input: 'number',
			  position : 'top',
			  inputAttributes: {
			    autocapitalize: 'off'
			  },
			  showCancelButton: true,
			  confirmButtonText: 'Retirar',
			  cancelButtonColor: '#F44336',
			  cancelButtonText: 'Cancelar',
			  showLoaderOnConfirm: true,
			  preConfirm: (cantidad) => {
				  if(cantidad<0 || !cantidad){
						Swal.fire({
							title : 'Error!',
							text : 'La cantidad debe ser mayor a 0',
							position : 'top',
							icon : 'error',
							confirmButtonText : 'OK!'
						})
				  }else{
					  $.post("/productos/modificarCantidad",
						 {
						  	"id":id,
						  	"tipo":"salida",
							"cantidad":cantidad
						},function(data){
							Swal.fire({
								title : 'Muy bien!',
								text : 'Cantidad actualizada',
								position : 'top',
								icon : 'success',
								confirmButtonText : 'OK!'
							}) 
							$("#tablaProductos").load("/productos/listaProductosFragment",function(data){
				            	 console.log("lista productos");
				            	 $("#nombreInfoPoduct").val("");
				            	 $("#costoInfoProduct").val(""); 
				            	 $("#precioVentaInfoProduct").val("");
				            	 $("#cantidadInfoProduct").val(""); 
				            	 $("#tipoActivoInfoProduct").val("");
				            	 $("#idSelectedProduct").val("");
						    	 addEvents();
				             });
						});
				  }
			  }
			});
	}
});

$("#eliminarProducto").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var id = $("#idSelectedProduct").val();
	if(!id || id == ""){
		Swal.fire({
			title : 'Error!',
			text : 'Debe seleccionar un producto',
			position : 'top',
			icon : 'error',
			confirmButtonText : 'Cool'
		})
	}else{
		Swal.fire({
			  title: 'Alerta!',
			  text: "Esta seguro de eliminar el producto?",
			  icon: 'warning',
			  position : 'top',
			  showCancelButton: true,
			  cancelButtonColor: '#F44336',
			  cancelButtonText: 'Cancelar',
			  confirmButtonColor: '#3085d6',
			  confirmButtonText: 'Ok!'
			}).then((result) => {
			  if (result.isConfirmed) {
				  $("#tablaProductos").load("/productos/deleteProductAndListFragment/"+id,function(data){
		            	 console.log("lista productos");
		            	 $("#nombreInfoPoduct").val("");
		            	 $("#costoInfoProduct").val(""); 
		            	 $("#precioVentaInfoProduct").val("");
		            	 $("#cantidadInfoProduct").val(""); 
		            	 $("#tipoActivoInfoProduct").val("");
		            	 $("#idSelectedProduct").val("");
				    	 addEvents();
		             });
			  }
			})
	}
});

$("#modificarProducto").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var id = $("#idSelectedProduct").val();
	if(!id || id == ""){
		Swal.fire({
			title : 'Error!',
			text : 'Debe seleccionar un producto',
			position : 'top',
			icon : 'error',
			confirmButtonText : 'Cool'
		})
	}else{
		$("#formUpdateProduct").load("/productos/modificarProducto/"+id,function(data){
			var activoFijo = $("#activoFijoProductoUpdate").val();
			if(activoFijo == 1){
				//Inventario
				$("#precioVentaProductoUpdate").show();
				$("#lblPrecioVentaUpdate").show();
			}else if(activoFijo == 2){
				//activo fijo
				$("#precioVentaProductoUpdate").hide();
				$("#precioVentaProductoUpdate").val("0");
				$("#lblPrecioVentaUpdate").hide();
			}else if(activoFijo == 3){
				//activo fijo
				$("#precioVentaProductoUpdate").hide();
				$("#lblPrecioVentaUpdate").hide();
			}
			$("#modalModificarProductos").modal("show");
			addEvents();
		});
	}
});

$("#btnListaProductos").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#modalModificarProductos").modal("hide");
	$("#modalAgregarProductos").modal("hide");
	$("#modalProductos").modal("show");
});

$("#agregarProducto").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#modalProductos").modal("hide");
	$("#modalAgregarProductos").modal("show");
});

$("#formUpdateProduct").on("submit", function (e) {
	e.preventDefault();
	e.stopImmediatePropagation();
	
	var nombre =  $("#nombreProductoUpdate").val();
	
	if(!nombre){
		$("#modalModificarProductos").modal("hide");
		 Swal.fire({
 			  title: 'Alerta!',
 			  text: "Los campos con '*' son requeridos",
 			  icon: 'warning',
 			  position : 'top',
 			  showCancelButton: false,
 			  confirmButtonColor: '#3085d6',
 			  confirmButtonText: 'Ok!'
 			}).then((result) => {
 			  if (result.isConfirmed) {
	             $("#modalModificarProductos").modal("show");
 			  }
 			})
	}else{
	       $.ajax({
	            url: "/productos/modificar/",
	            type: "POST",
	            data: new FormData(this),
	            enctype: 'multipart/form-data',
	            processData: false,
	            contentType: false,
	            cache: false,
	            success: function (res) {
	            	$("#modalModificarProductos").modal("hide");
		            if(res=="1"){
			            Swal.fire({
				  			  title: 'Muy bien!',
				  			  text: "Registro modificado",
				  			  icon: 'success',
				  			  position : 'top',
				  			  showCancelButton: false,
				  			  confirmButtonColor: '#3085d6',
				  			  confirmButtonText: 'Ok!'
				  			}).then((result) => {
				  			  if (result.isConfirmed) {
				  	             $("#nombreProductoAdd").val("");
					             $("#costoProductoAdd").val("");
					             $("#precioVentaProductoAdd").val("");
					             $("#imagenProductoAdd").val("");
					             $("#modalAgregarProductos").modal("show");
				  			  }
				  			})
					}else{
						Swal.fire({
							title : 'Error!',
							text : 'No se pudo Modificar el registro',
							position : 'top',
							icon : 'error',
							confirmButtonText : 'Cool'
						})
					}

		            $("#tablaProductos").load("/productos/listaProductosFragment",function(data){
		            	 console.log("lista productos");
		            	 $("#nombreInfoPoduct").val("");
		            	 $("#costoInfoProduct").val(""); 
		            	 $("#precioVentaInfoProduct").val("");
		            	 $("#cantidadInfoProduct").val(""); 
		            	 $("#tipoActivoInfoProduct").val("");
		            	 $("#idSelectedProduct").val("");
				    	 addEvents();
		             });
	            },
	            error: function (err) {
	                console.error(err);
	                $("#modalModificarProductos").modal("hide");
	                Swal.fire({
						title : 'Error!',
						text : 'No se pudo completar la operacion, intente mas tarde',
						position : 'top',
						icon : 'error',
						confirmButtonText : 'Cool'
					})
	            }
	        });
	}
});

$('#productosSelectForCompra').on("select2:select", function(e) { 
	e.preventDefault();
	e.stopImmediatePropagation();
	var producto = $("#productosSelectForCompra").val();
	if(producto && producto!=''){
		$.get("/cuentasContables/cuentaDelProducto/"+producto,function(data){
			$("#cuentaContableCompra").val(data);
			addEvents();
		});
	}else{
		$("#cuentaContableCompra").val("");
	}
});

$("#agregarItemsCompra").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var producto = $("#productosSelectForCompra").val();
	var cantidad = $("#cantidadProductoCompra").val(); 
	var costo = $("#costoProductoCompra").val(); 
	var txt = $("#txtServicioConsumo").val(); 
	//Producto, cantidad y costo requerido
	if(producto != "" && cantidad != "" && costo != "" || txt != ""){
		$("#tablaCompras").load("/productos/addProductosTempForCompras",
			{
				"idProducto": producto,
				"cantidad": cantidad,
				"costo": costo,
				"txt": txt
			},function(data){
				addEvents();
				$('#productosSelectForCompra').val('').trigger('change.select2');
//				$('#productosSelectForCompra').select2('focus');
				$("#cantidadProductoCompra").val(""); 
				$("#costoProductoCompra").val(""); 
				var suplidor = $("#selectSuplidorForProduct").val();
				if(suplidor != ""){
					$("#cuentasDelSuplidor").show();
					$("#cuentasDelSuplidor").load("/suplidores/cuentasDelSuplidor/"+suplidor,
						function(data){
						$.get("/compras/totalesCompras/"+suplidor,
								function(data){
									$("#subTotalCompra").val(data[0]);
									$("#totalPreCompra").val(data[1]);
									addEvents();
								});
						});
				}else{
					$("#cuentasDelSuplidor").hide();
				}
			});
	}else{
		  Swal.fire({
				title : 'Alerta!',
				text : 'El producto, cantidad, referencia y costo son requeridos',
				position : 'top',
				icon : 'warning',
				confirmButtonText : 'Cool'
			})
	}
});

$("#selectSuplidorForProduct").change(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var suplidor = $("#selectSuplidorForProduct").val();
	if(suplidor != ""){
		$("#cuentasDelSuplidor").show();
		$("#cuentasDelSuplidor").load("/suplidores/cuentasDelSuplidor/"+suplidor,
			function(data){
				$.get("/compras/totalesCompras/"+suplidor,
					function(data){
						$("#subTotalCompra").val(data[0]);
						$("#totalPreCompra").val(data[1]);
						addEvents();
					});
			});
	}else{
		$("#subTotalCompra").val($("#totalProductoTemp").text());
		$("#totalPreCompra").val($("#totalProductoTemp").text());
		$("#cuentasDelSuplidor").hide();
	}
});

$("#guardarCompra").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var totalPreCompra = $("#totalPreCompra").val();
	var totalProductoTemp = $("#totalProductoTemp").text();
	var fecha = $("#fechaCompra").val();
	var suplidor = $("#selectSuplidorForProduct").val();
	var factura = $("#facturaCompra").val();
	var comprobante = $("#comprobanteCompra").val();
	var tipo = $("#tipoCompra").val();
	var subTotal = $("#subTotalCompra").val();
	var cuentaContableId = $("#cuentaContableSelectForCompra").val();
	
	var impuestosAcct = [];
	var valorImpuestosAcct = [];
	var idCuentaContable = [];
	
	$('#cuentasDelSuplidor td input').each(function (i) {
		  if($(this).hasClass("impuesto")){
			  impuestosAcct.push($(this).val());
		  }
    });
	
	$('#cuentasDelSuplidor td').each(function (i) {
		  if($(this).hasClass("oculto")){
			  idCuentaContable.push($(this).text());
		  }
	});

	$('#cuentasDelSuplidor td').each(function (i) {
		  if($(this).hasClass("valorImpuesto")){
			  valorImpuestosAcct.push($(this).text());
		  }
    });
	
	console.log(impuestosAcct);
	console.log(valorImpuestosAcct);
	console.log(idCuentaContable);
		
	$.post("/compras/guardarCompra",
			 {
				 "total":totalPreCompra,
				 "fecha":fecha,
				 "suplidor":suplidor,
				 "factura":factura,
				 "comprobante":comprobante,
				 "tipo":tipo,
				 "subTotal":subTotal,
				 "cuentaContableId":cuentaContableId,
				 "tipo":tipo,
				 "impuestos":impuestosAcct.toString(),
				 "valorImpuestos":valorImpuestosAcct.toString(),
				 "idCuentaContable":idCuentaContable.toString()
			 },function(data){
				 if(data == "1"){
					 Swal.fire({
						title : 'Muy bien!',
						text : 'Compra creada correctamente',
						position : 'top',
						icon : 'success',
						confirmButtonText : 'Cool'
					})
					$("#contenido").load("/contabilidad/compras",function(data){
						console.log("Compras");
						addEvents();
					});
				}else{
					Swal.fire({
					title : 'Alerta!',
					text : 'No se pudo guardar la compra',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			 }
		});
});

$("#agregarSuplidor").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#modalAgregarSuplidor").modal("show");
});

$("#guardarSuplidor").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var nombre = $("#nombreSuplidor").val();
	var telefono = $("#telefonoSuplidor").val();
	var direccion = $("#direccionSuplidor").val();
	var rnc = $("#rncSuplidor").val();
	
	if(!nombre || !telefono || !direccion || !rnc){
		$("#modalAgregarSuplidor").modal("hide");
		 Swal.fire({
			  title: 'Alerta!',
			  text: "Todos los campos son requeridos",
			  icon: 'warning',
			  position : 'top',
			  showCancelButton: false,
			  confirmButtonColor: '#3085d6',
			  confirmButtonText: 'Ok!'
			}).then((result) => {
			  if (result.isConfirmed) {
	             $("#modalAgregarSuplidor").modal("show");
			  }
			})
	}else{
		$("#selectSuplidorForProduct").load("/suplidores/agregarSuplidorForCompra",
			{
				"nombre":nombre,
				"telefono":telefono,
				"direccion":direccion,
				"rnc":rnc
			},function(data){
				console.log("Lista de suplidores actualizada");
				$("#modalAgregarSuplidor").modal("hide");
				$("#nombreSuplidor").val("");
				$("#telefonoSuplidor").val("");
				$("#direccionSuplidor").val("");
				$("#rncSuplidor").val("");
				Swal.fire({
					title : 'Muy bien!',
					text : 'Suplidor creado correctamente',
					position : 'top',
					icon : 'success',
					confirmButtonText : 'Cool'
				})
				$("#tablaCuentasTempSuplidor").load("/suplidores/borrarSuplidoresTemp",function(data){
					addEvents();
				});
		});
	}
	
});

$("#findByTipoProducto").change(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var nombreProducto = $("#findByNombreProducto").val();
	var tipo = $("#findByTipoProducto").val();
	$("#tablaProductos").load("/productos/listaProductosFragmentFindByAndPagination/",
			{
				"nombre": nombreProducto,
				"tipo": tipo
			},function(data){
				$("#nombreInfoPoduct").val("");
	           	 $("#costoInfoProduct").val(""); 
	           	 $("#precioVentaInfoProduct").val("");
	           	 $("#cantidadInfoProduct").val(""); 
	          	 $("#tipoActivoInfoProduct").val("");
	         	 $("#idSelectedProduct").val("");
		    addEvents();
	});
});

$("#findByNombreProducto").on("keyup", function(e) {
	e.preventDefault();
	e.stopImmediatePropagation();
	var nombreProducto = $("#findByNombreProducto").val();
	var tipo = $("#findByTipoProducto").val();
	
	$("#tablaProductos").load("/productos/listaProductosFragmentFindByAndPagination/",
			{
				"nombre": nombreProducto,
				"tipo": tipo
			},function(data){
				$("#nombreInfoPoduct").val("");
	           	 $("#costoInfoProduct").val(""); 
	           	 $("#precioVentaInfoProduct").val("");
	           	 $("#cantidadInfoProduct").val(""); 
	          	 $("#tipoActivoInfoProduct").val("");
	         	 $("#idSelectedProduct").val("");
		    addEvents();
	});
	
});
	
$("#activoFijoProductoUpdate").change(function(e){
	var activoFijo = $("#activoFijoProductoUpdate").val();
	if(activoFijo == 1){
		//Inventario
		$("#precioVentaProductoUpdate").show();
		$("#lblPrecioVentaUpdate").show();
	}else if(activoFijo == 2){
		//activo fijo
		$("#precioVentaProductoUpdate").hide();
		$("#precioVentaProductoUpdate").val("0");
		$("#lblPrecioVentaUpdate").hide();
	}else if(activoFijo == 3){
		//activo fijo
		$("#precioVentaProductoUpdate").hide();
		$("#lblPrecioVentaUpdate").hide();
	}
});

$("#activoFijoProductoAdd").change(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var activoFijo = $("#activoFijoProductoAdd").val();
	if(activoFijo == 1){
		//Inventario
		$("#precioVentaProductoAdd").show();
		$("#lblPrecioVentaAdd").show();
	}else if(activoFijo == 2){
		//activo fijo
		$("#precioVentaProductoAdd").hide();
		$("#lblPrecioVentaAdd").hide();
	}else if(activoFijo == 3){
		//activo fijo
		$("#precioVentaProductoAdd").hide();
		$("#lblPrecioVentaAdd").hide();
	}
});

$("#formAddProductCompra").on("submit", function (e) {
	e.preventDefault();
	e.stopImmediatePropagation();
	
	var nombre =  $("#nombreProductoAdd").val();
	var cuentaContable = $("#cuentaContableAdd").val();
	var activoFijo = $("#activoFijoProductoAdd").val();
	
	if(cuentaContable == ""){
		$("#modalAgregarProductosCompras").modal("hide");
		 Swal.fire({
			  title: 'Alerta!',
			  text: "La cuenta contable es requerida",
			  icon: 'warning',
			  position : 'top',
			  showCancelButton: false,
			  confirmButtonColor: '#3085d6',
			  confirmButtonText: 'Ok!'
			}).then((result) => {
			  if (result.isConfirmed) {
	             $("#modalAgregarProductosCompras").modal("show");
			  }
			})
	}else{
		if(!nombre){
			$("#modalAgregarProductosCompras").modal("hide");
			 Swal.fire({
	 			  title: 'Alerta!',
	 			  text: "Los campos con '*' son requeridos",
	 			  icon: 'warning',
	 			  position : 'top',
	 			  showCancelButton: false,
	 			  confirmButtonColor: '#3085d6',
	 			  confirmButtonText: 'Ok!'
	 			}).then((result) => {
	 			  if (result.isConfirmed) {
		             $("#modalAgregarProductosCompras").modal("show");
	 			  }
	 			})
		}else{
		       $.ajax({
		            url: "/productos/crear/",
		            type: "POST",
		            data: new FormData(this),
		            enctype: 'multipart/form-data',
		            processData: false,
		            contentType: false,
		            cache: false,
		            success: function (res) {
		            	$("#modalAgregarProductosCompras").modal("hide");
			            if(res=="1"){
				            Swal.fire({
					  			  title: 'Muy bien!',
					  			  text: "Registro guardado",
					  			  icon: 'success',
					  			  position : 'top',
					  			  showCancelButton: false,
					  			  confirmButtonColor: '#3085d6',
					  			  confirmButtonText: 'Ok!'
					  			}).then((result) => {
					  			  if (result.isConfirmed) {
					  	             $("#nombreProductoAdd").val("");
						             $("#costoProductoAdd").val("");
						             $("#cantidadProductoAdd").val("");
						             $("#precioVentaProductoAdd").val("");
						             $("#imagenProductoAdd").val("");
						             $("#cantidadProductoAdd").val(""); 
						             $("#modalAgregarProductosCompras").modal("show");
					  			  }
					  			})
						}else{
							Swal.fire({
								title : 'Error!',
								text : 'No se pudo crear el registro',
								position : 'top',
								icon : 'error',
								confirmButtonText : 'Cool'
							})
						}

			            if($("#inventarioRadio").is(':checked')){
			        		$("#productosSelectForCompra").load("/productos/reloadProductsCompra/"+1,function(data){
			        			addEvents();
			        		});
			            }else if($("#servicioRadio").is(':checked')){
			            	$("#productosSelectForCompra").load("/productos/reloadProductsCompra/"+23,function(data){
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
		}
	}
});

$("#formAddProduct").on("submit", function (e) {
	e.preventDefault();
	e.stopImmediatePropagation();
	
	var nombre =  $("#nombreProductoAdd").val();
	var cuentaContable = $("#cuentaContableAdd").val();
	
	if(cuentaContable == ""){
		$("#modalAgregarProductos").modal("hide");
		 Swal.fire({
			  title: 'Alerta!',
			  text: "La cuenta contable es requerida",
			  icon: 'warning',
			  position : 'top',
			  showCancelButton: false,
			  confirmButtonColor: '#3085d6',
			  confirmButtonText: 'Ok!'
			}).then((result) => {
			  if (result.isConfirmed) {
	             $("#modalAgregarProductos").modal("show");
			  }
			})
	}else{
		if(!nombre){
			$("#modalAgregarProductos").modal("hide");
			 Swal.fire({
	 			  title: 'Alerta!',
	 			  text: "Los campos con '*' son requeridos",
	 			  icon: 'warning',
	 			  position : 'top',
	 			  showCancelButton: false,
	 			  confirmButtonColor: '#3085d6',
	 			  confirmButtonText: 'Ok!'
	 			}).then((result) => {
	 			  if (result.isConfirmed) {
		             $("#modalAgregarProductos").modal("show");
	 			  }
	 			})
		}else{
		       $.ajax({
		            url: "/productos/crear/",
		            type: "POST",
		            data: new FormData(this),
		            enctype: 'multipart/form-data',
		            processData: false,
		            contentType: false,
		            cache: false,
		            success: function (res) {
		            	$("#modalAgregarProductos").modal("hide");
			            if(res=="1"){
				            Swal.fire({
					  			  title: 'Muy bien!',
					  			  text: "Registro guardado",
					  			  icon: 'success',
					  			  position : 'top',
					  			  showCancelButton: false,
					  			  confirmButtonColor: '#3085d6',
					  			  confirmButtonText: 'Ok!'
					  			}).then((result) => {
					  			  if (result.isConfirmed) {
					  	             $("#nombreProductoAdd").val("");
						             $("#costoProductoAdd").val("");
						             $("#precioVentaProductoAdd").val("");
						             $("#imagenProductoAdd").val("");
						             $("#modalAgregarProductos").modal("show");
					  			  }
					  			})
						}else{
							Swal.fire({
								title : 'Error!',
								text : 'No se pudo crear el registro',
								position : 'top',
								icon : 'error',
								confirmButtonText : 'Cool'
							})
						}

			             $("#tablaProductos").load("/productos/listaProductosFragment",function(data){
			            	 console.log("lista productos");
			            	 $("#nombreInfoPoduct").val("");
			            	 $("#costoInfoProduct").val(""); 
			            	 $("#precioVentaInfoProduct").val("");
			            	 $("#cantidadInfoProduct").val(""); 
			            	 $("#tipoActivoInfoProduct").val("");
			            	 $("#idSelectedProduct").val("");
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
		}
	}
});

$("#tarjetaEntradaDiario").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#modalEntradaDiarioDC").modal("show");
});

$("#agregarEntradaDiario").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$.post("/contabilidad/guardarEntradasDiario", function(data){
		 if(data == "1"){
			 $("#modalEntradaDiarioDC").modal("hide");
				Swal.fire({
					  title: 'Muy bien!',
					  text: "Entradas creadas correctamente",
					  icon: 'success',
					  position : 'top',
					  showCancelButton: false,
					  confirmButtonColor: '#3085d6',
					  confirmButtonText: 'Ok!'
					}).then((result) => {
					  if (result.isConfirmed) {
						  $("#montoEntradaDiario").val("");
						  $("#referenciaEntradaDiario").val("");
						  $("#modalEntradaDiarioDC").modal("hide");
						  $("#contenido").load("/contabilidad/mostrarContabilidad",function(data){
								console.log("Contabilidad");
								addEvents();
						  });
					  }
					})
		 }else if(data == "0"){
			 $("#modalEntradaDiarioDC").modal("hide");
				Swal.fire({
					  title: 'Alerta!',
					  text: "No se guardaron las entradas del diario",
					  icon: 'warning',
					  position : 'top',
					  showCancelButton: false,
					  confirmButtonColor: '#3085d6',
					  confirmButtonText: 'Ok!'
					}).then((result) => {
					  if (result.isConfirmed) {
						  $("#montoEntradaDiario").val("");
						 $("#referenciaEntradaDiario").val("");
						 $("#modalEntradaDiarioDC").modal("hide");
					  }
					})
		 }else if(data == "2"){
			 $("#modalEntradaDiarioDC").modal("hide");
				Swal.fire({
					  title: 'Alerta!',
					  text: "Los totales deben ser iguales",
					  icon: 'warning',
					  position : 'top',
					  showCancelButton: false,
					  confirmButtonColor: '#3085d6',
					  confirmButtonText: 'Ok!'
					}).then((result) => {
					  if (result.isConfirmed) {
						  $("#montoEntradaDiario").val("");
						 $("#referenciaEntradaDiario").val("");
						 $("#modalEntradaDiarioDC").modal("show");
					  }
					})
		 }
	 });
});

$("#codigoEntradaDiario").on("keyup", function(e) {
	e.preventDefault();
	e.stopImmediatePropagation();
	var valor = $("#codigoEntradaDiario").val();
	$("#cuentaContableAuxiliarEd").load("/cuentasContables/buscarContablesAuxiliarEntradaDiario",{
			"valor":valor
		},function(data){
			console.log("Lista actualizada");
		});
});

$("#agregarEntradaDiarioTemp").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var monto = $("#montoEntradaDiario").val();
	var referencia = $("#referenciaEntradaDiario").val();
	var cuentaContableId = $("#cuentaContableAuxiliarEd").val(); 
	var tipo = $("#tipoEntradaDiario").val(); 
	
	if(!monto || !referencia || !cuentaContableId || !tipo){
		$("#modalEntradaDiarioDC").modal("hide");
		Swal.fire({
			  title: 'Alerta!',
			  text: "Todos los campos son requeridos",
			  icon: 'warning',
			  position : 'top',
			  showCancelButton: false,
			  confirmButtonColor: '#3085d6',
			  confirmButtonText: 'Ok!'
			}).then((result) => {
			  if (result.isConfirmed) {
				  $("#modalEntradaDiarioDC").modal("show");
			  }
			})
	}else{
		$("#tablaEntradasTemp").load("/contabilidad/guardarTempEntradaDiario",
			{
				"monto":monto,
				"referencia":referencia,
				"cuentaContableId":cuentaContableId,
				"tipo":tipo
			},function(data){
				$("#montoEntradaDiario").val("");
				$("#referenciaEntradaDiario").val("");
				addEvents();
		});
	}
});

$("#btnCarpetaContabilidad").click(function(e){
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
		            url: "/carpetas/buscarCarpetaContabilidad/"+carpeta,
		            type: "GET",
		            processData: false,
		            contentType: false,
		            cache: false,
		            success: function (res) {
						$("#buscarCarpeta").replaceWith(res);
		            	var msg = $("#msgId").val();
			            if(msg=="0"){
							Swal.fire({
								title : 'No tienes carpetas creadas!',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
							addEvents();
						}else{
							Swal.fire({
								title : 'Muy bien!',
								text : 'Carpeta Seleccionada',
								position : 'top',
								icon : 'success',
								confirmButtonText : 'Cool'
							})
		
							$("#contenido").load("/contabilidad/mostrarContabilidad",function(data){
								console.log("Contabilidad");
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

$("#btnExitCarpetaContabilidad").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	  $.ajax({
          url: "/carpetas/buscarCarpetaContabilidad",
          type: "GET",
          processData: false,
          contentType: false,
          cache: false,
          success: function (res) {
				$("#buscarCarpeta").replaceWith(res);
          	var msg = $("#msgId").val();
	            if(msg=="0"){
					addEvents();
					location.href = '/logout';
				}else{
					$("#contenido").load("/contabilidad/mostrarContabilidad",function(data){
						console.log("Contabilidad");
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
});	

$("#btnEntradaDiarios").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#modalEntradaDiario").modal("show");
	addEvents();
});

$("#indicioEd1").on("keyup", function(e) {
	e.preventDefault();
	e.stopImmediatePropagation();
	var valor = $("#indicioEd1").val();
	$("#cuentaContableAuxiliarEd1").load("/cuentasContables/buscarContablesAuxiliar1",{
			"valor":valor
		},function(data){
			console.log("Lista actualizada");
		});
});

$("#indicioEd2").on("keyup", function(e) {
	e.preventDefault();
	e.stopImmediatePropagation();
	var valor = $("#indicioEd2").val();
	$("#cuentaContableAuxiliarEd2").load("/cuentasContables/buscarContablesAuxiliar2",{
			"valor":valor
		},function(data){
			console.log("Lista actualizada");
		});
});

$("#btnGuardarEntradaDiario").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var monto = $("#montoEd").val();
	var motivo = $("#motivoEd").val();
	var cuenta1 = $("#cuentaContableAuxiliarEd1").val();
	var cuenta2 = $("#cuentaContableAuxiliarEd2").val();
	var tipo1 =  $("#tipoEd1").val();
	var tipo2 =  $("#tipoEd2").val();
	
	if(!monto || !motivo || !cuenta1 || !cuenta2){
		$("#modalEntradaDiario").modal("hide");
		Swal.fire({
			  title: 'Alerta!',
			  text: "Todos los campos con '*' son requeridos",
			  icon: 'warning',
			  position : 'top',
			  showCancelButton: false,
			  confirmButtonColor: '#3085d6',
			  confirmButtonText: 'Ok!'
			}).then((result) => {
			  if (result.isConfirmed) {
				  $("#modalEntradaDiario").modal("show");
			  }
			})
	}else{
		if(cuenta1 == cuenta2){
			Swal.fire({
				  title: 'Alerta!',
				  text: "Las cuentas contables no pueden ser iguales",
				  icon: 'warning',
				  position : 'top',
				  showCancelButton: false,
				  confirmButtonColor: '#3085d6',
				  confirmButtonText: 'Ok!'
				}).then((result) => {
				  if (result.isConfirmed) {
					  $("#modalEntradaDiario").modal("show");
				  }
				})
		}else{
			$.post("/cuentasContables/guardarEntradaDiario",
					 {
						 "monto":monto,
						 "motivo":motivo,
						 "cuenta1":cuenta1,
						 "cuenta2":cuenta2
					 },function(data){
							 if(data == "1"){
									$("#modalEntradaDiario").modal("hide");
									Swal.fire({
										  title: 'Muy bien!',
										  text: "Entrada creada correctamente",
										  icon: 'success',
										  position : 'top',
										  showCancelButton: false,
										  confirmButtonColor: '#3085d6',
										  confirmButtonText: 'Ok!'
										}).then((result) => {
										  if (result.isConfirmed) {
											  $("#contenido").load("/contabilidad/mostrarContabilidad",function(data){
												console.log("Contabilidad");
												addEvents();
											});
										  }
										})
							 }else{
								 $("#modalEntradaDiario").modal("hide");
								 Swal.fire({
									  title: 'Alerta!',
									  text: "No se pudo guardar la entrada",
									  icon: 'warning',
									  position : 'top',
									  showCancelButton: false,
									  confirmButtonColor: '#3085d6',
									  confirmButtonText: 'Ok!'
									}).then((result) => {
									  if (result.isConfirmed) {
										  $("#contenido").load("/contabilidad/mostrarContabilidad",function(data){
												console.log("Contabilidad");
												addEvents();
											});
									  }
									})
							 }
							 $("#montoEd").val("");
							 $("#motivoEd").val("");
					 });
		}
	}
	
});


$("#btnIniciarContabilidad").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#cuentaContableAuxiliar").load("/cuentasContables/cuentasContablesAuxiliar",
		function(data){
			$("#modalCuentasAuxiliares").modal("show");
			addEvents();
		});
});

$("#btnGuardarInicioContable").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var idCuentaContable = $("#cuentaContableAuxiliar").val();
	var tipoCuentaAuxiliar = $("#tipoCuentaAuxiliar").val();
	var montoCuentaAuxiliar = $("#montoCuentaAuxiliar").val();
		
	$.post("/cuentasContables/iniciarContabilidad",
	 {
		 "idCuentaContable":idCuentaContable,
		 "tipo":tipoCuentaAuxiliar,
		 "monto":montoCuentaAuxiliar
	 },function(data){
		 if(data == "1"){
			 $("#modalCuentasAuxiliares").modal("hide");
				Swal.fire({
					  title: 'Muy Bien!',
					  text: "Guardado con exito",
					  icon: 'success',
					  position : 'top',
					  showCancelButton: false,
					  confirmButtonColor: '#3085d6',
					  confirmButtonText: 'Ok!'
					}).then((result) => {
					  if (result.isConfirmed) {
						 $("#contenido").load("/contabilidad/mostrarContabilidad",function(data){
								console.log("Contabilidad");
								addEvents();
						});
					  }
					})
		 	}else if(data == "0"){
		 		$("#modalCuentasAuxiliares").modal("hide");
				Swal.fire({
					  title: 'Alerta!',
					  text: "No se pudo iniciar la contabilidad",
					  icon: 'warning',
					  position : 'top',
					  showCancelButton: false,
					  confirmButtonColor: '#3085d6',
					  confirmButtonText: 'Ok!'
					}).then((result) => {
					  if (result.isConfirmed) {
						$("#contenido").load("/contabilidad/mostrarContabilidad",function(data){
							console.log("Contabilidad");
							addEvents();
						});
					  }
					})
		 	}
		});
	
	addEvents();
});

$("#imprime").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	impresionTabla("tablaCuentasContables");
});

$("#imprime2").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	$.get("/cuentasContables/imprimirCuentasContables",function(data){
		impresion(data);
		addEvents();
	});
});

$("#editCodigoCuenta").on('keyup', function(e) { 
	e.stopImmediatePropagation();
	
	var valor = $('#editCodigoCuenta').val();
	var res = valor.match(/^[0-9-]+$/);
		
	if(res == null){
		 $('#editCodigoCuenta').val(valor.substring(0, valor.length - 1));
	}

	var codigoCuenta = $("#editCodigoCuenta").val();
	var cant = codigoCuenta.length;
	if(cant == 1){
		$.get("/grupoCuentas/buscarGrupoCuenta/"+codigoCuenta,
			function(data){
				if(data != "0"){
					$("#editGrupoCuenta").val(data);
				}else{
					$("#editGrupoCuenta").val("");
				}
			addEvents();
		});
	}
	
	if(cant == 0){
		$("#editGrupoCuenta").val("");
	}
	
	if(cant > 1){
		$.get("/cuentasContables/buscarCuentaControl/"+codigoCuenta,
				function(data){
					if(data[1] != "0"){
						$("#editCuentaControl").val(data[0]);
						$("#idEditCuentaControl").val(data[1]);
					}else{
						$("#editCuentaControl").val("");
						$("#idEditCuentaControl").val("0");
					}
			addEvents();
		});
	}
});

$("#codigoCuenta").on('keyup', function(e) { 
	e.stopImmediatePropagation();
	
	var valor = $('#codigoCuenta').val();
	var res = valor.match(/^[0-9-]+$/);
		
	if(res == null){
		 $('#codigoCuenta').val(valor.substring(0, valor.length - 1));
	}

	var codigoCuenta = $("#codigoCuenta").val();
	var cant = codigoCuenta.length;
	if(cant == 1){
		$.get("/grupoCuentas/buscarGrupoCuenta/"+codigoCuenta,
			function(data){
				if(data != "0"){
					$("#grupoCuenta").val(data);
				}else{
					$("#grupoCuenta").val("");
				}
			addEvents();
		});
	}
	
	if(cant == 0){
		$("#grupoCuenta").val("");
	}
	
	if(cant > 1){
		$.get("/cuentasContables/buscarCuentaControl/"+codigoCuenta,
				function(data){
					if(data[1] != "0"){
						$("#cuentaControl").val(data[0]);
						$("#idCuentaControl").val(data[1]);
					}else{
						$("#cuentaControl").val("");
						$("#idCuentaControl").val("0");
					}
			addEvents();
		});
	}
});

$("#btnCrearCuentas").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#tablaCuentasContables").load("/cuentasContables/mostrarCuentasContables",function(data){
		console.log("Cuentas Contables");
		$("#modalCuentas").modal("show");
		addEvents();
	});
});

$("#agregarCuentaContable").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var tipoCuenta = $("#tipoCuenta").val();
	var grupoCuenta = $("#grupoCuenta").val();
	var codigoCuenta = $("#codigoCuenta").val();
	var nombreCuenta = $("#nombreCuenta").val();
	var cuentaControl = $("#cuentaControl").val();
	var idCuentaControl = $("#idCuentaControl").val();
	if(tipoCuenta != 0 && grupoCuenta!="" && nombreCuenta!=""){
		 $.post("/cuentasContables/crear",{
			 "tipoCuenta":tipoCuenta,
			 "grupoCuenta":grupoCuenta,
			 "codigoCuenta":codigoCuenta,
			 "nombreCuenta":nombreCuenta,
			 "cuentaControl":cuentaControl,
			 "idCuentaControl":idCuentaControl
		 },function(data){
				$("#grupoCuenta").val("");
				$("#codigoCuenta").val("");
				$("#nombreCuenta").val("");
				$("#cuentaControl").val("");
				$("#idCuentaControl").val("");
			 	if(data == "1"){
			 		$("#tablaCuentasContables").load("/cuentasContables/mostrarCuentasContables",function(data){
			 			console.log("Cuentas Contables");
			 			addEvents();
			 		});
			 	}else if(data == "-1"){
			 		$("#modalCuentas").modal("hide");
					Swal.fire({
						  title: 'Alerta!',
						  text: "No se puede agregar esta cuenta",
						  icon: 'warning',
						  position : 'top',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  confirmButtonText: 'Ok!'
						}).then((result) => {
						  if (result.isConfirmed) {
							$("#modalCuentas").modal("show");
						  }
						})
			 	}else if(data == "-2"){
			 		$("#modalCuentas").modal("hide");
					Swal.fire({
						  title: 'Alerta!',
						  text: "No puede agregar cuentas a cuentas auxiliares",
						  icon: 'warning',
						  position : 'top',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  confirmButtonText: 'Ok!'
						}).then((result) => {
						  if (result.isConfirmed) {
							$("#modalCuentas").modal("show");
						  }
						})
			 	}else if(data == "-3"){
			 		$("#modalCuentas").modal("hide");
			 		Swal.fire({
						  title: 'Alerta!',
						  text: "No puede agregar esta cuenta",
						  icon: 'warning',
						  position : 'top',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  confirmButtonText: 'Ok!'
						}).then((result) => {
						  if (result.isConfirmed) {
							$("#modalCuentas").modal("show");
						  }
						})
			 	}else{
			 		//No guardo
			 		$("#modalCuentas").modal("hide");
					Swal.fire({
						  title: 'Alerta!',
						  text: "No se guardo la cuenta contable",
						  icon: 'warning',
						  position : 'top',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  confirmButtonText: 'Ok!'
						}).then((result) => {
						  if (result.isConfirmed) {
							$("#modalCuentas").modal("show");
						  }
						})
			 	}
				addEvents();
		 });
	}else{
		$("#modalCuentas").modal("hide");
		Swal.fire({
			  title: 'Alerta!',
			  text: "El nombre, el tipo y el Grupo cuenta no pueden estar vacio",
			  icon: 'warning',
			  position : 'top',
			  showCancelButton: false,
			  confirmButtonColor: '#3085d6',
			  confirmButtonText: 'Ok!'
			}).then((result) => {
			  if (result.isConfirmed) {
				$("#modalCuentas").modal("show");
			  }
			})
	}
});

$("#btnCuentaEnlace").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#modalCuentaEnlace").modal("show");
});

$("#btnGuardarCuentaEnlace").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var capitalPrestamoCC = $("#capitalPrestamoCC").val();
	var interesPrestamoCC = $("#interesPrestamoCC").val();
	var gastosCierrePrestamoCC = $("#gastosCierrePrestamoCC").val();
	var cobrosAdicionalesPrestamoCC = $("#cobrosAdicionalesPrestamoCC").val();
	
	if(!capitalPrestamoCC || !interesPrestamoCC || !gastosCierrePrestamoCC || !cobrosAdicionalesPrestamoCC){
		 $("#modalCuentaEnlace").modal("hide");
			Swal.fire({
				  title: 'Alerta!',
				  text: "Los campos con '*' no pueden estar vacios",
				  icon: 'warning',
				  position : 'top',
				  showCancelButton: false,
				  confirmButtonColor: '#3085d6',
				  confirmButtonText: 'Ok!'
				}).then((result) => {
				  if (result.isConfirmed) {
					$("#modalCuentaEnlace").modal("show");
				  }
				})
	}else{
		if(capitalPrestamoCC == interesPrestamoCC || capitalPrestamoCC == gastosCierrePrestamoCC || capitalPrestamoCC ==  cobrosAdicionalesPrestamoCC){
			 $("#modalCuentaEnlace").modal("hide");
			Swal.fire({
				  title: 'Alerta!',
				  text: "Las cuentas contables no pueden ser repetidas",
				  icon: 'warning',
				  position : 'top',
				  showCancelButton: false,
				  confirmButtonColor: '#3085d6',
				  confirmButtonText: 'Ok!'
				}).then((result) => {
				  if (result.isConfirmed) {
					$("#modalCuentaEnlace").modal("show");
				  }
				})
		}else{
			 $.post("/cuentasContables/guardarCuentaEnlace",{
				 "capitalPrestamo":capitalPrestamoCC,
				 "interesPrestamo":interesPrestamoCC,
				 "gastosCierrePrestamo":gastosCierrePrestamoCC,
				 "cobrosAdicionalesPrestamo":cobrosAdicionalesPrestamoCC
			 },function(data){
				 if(data == "1"){
					 $("#modalCuentaEnlace").modal("hide");
						Swal.fire({
							  title: 'Muy Bien!',
							  text: "Registros guardados",
							  icon: 'success',
							  position : 'top',
							  showCancelButton: false,
							  confirmButtonColor: '#3085d6',
							  confirmButtonText: 'Ok!'
							}).then((result) => {
							  if (result.isConfirmed) {
//								$("#modalCuentaEnlace").modal("show");
							  }
							})
				 }
			 });
		}
	}
});

$("#codigoCapitalPrestamoCC").on("keyup", function(e) {
	e.preventDefault();
	e.stopImmediatePropagation();
	var valor = $("#codigoCapitalPrestamoCC").val();
	$("#capitalPrestamoCC").load("/cuentasContables/buscarContablesAuxiliarCapitalPrestamo",{
			"valor":valor
		},function(data){
			console.log("Lista actualizada");
		});
});

$("#codigoInteresPrestamoCC").on("keyup", function(e) {
	e.preventDefault();
	e.stopImmediatePropagation();
	var valor = $("#codigoInteresPrestamoCC").val();
	$("#interesPrestamoCC").load("/cuentasContables/buscarContablesAuxiliarInteresPrestamo",{
			"valor":valor
		},function(data){
			console.log("Lista actualizada");
		});
});

$("#codigoGastosCierrePrestamoCC").on("keyup", function(e) {
	e.preventDefault();
	e.stopImmediatePropagation();
	var valor = $("#codigoGastosCierrePrestamoCC").val();
	$("#gastosCierrePrestamoCC").load("/cuentasContables/buscarContablesAuxiliarGastosCierrePrestamo",{
			"valor":valor
		},function(data){
			console.log("Lista actualizada");
		});
});

$("#codigoCobrosAdicionalesPrestamoCC").on("keyup", function(e) {
	e.preventDefault();
	e.stopImmediatePropagation();
	var valor = $("#codigoCobrosAdicionalesPrestamoCC").val();
	$("#cobrosAdicionalesPrestamoCC").load("/cuentasContables/buscarContablesAuxiliarCobrosAdicionalesPrestamo",{
			"valor":valor
		},function(data){
			console.log("Lista actualizada");
		});
});

$("#btnGuardarUpdateCuentaContable").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var id = $("#idEditCuentaControlPrincipal").val();
	var codigoCuenta = $("#editCodigoCuenta").val();
	var nombreCuenta = $("#editNombreCuenta").val();
	var tipoCuenta = $("#editTipoCuenta").val();
	var cuentaControl = $("#editCuentaControl").val();
	var grupoCuenta = $("#editGrupoCuenta").val();
	var idCuentaControl = $("#idEditCuentaControl").val();
	
	if(tipoCuenta != 0 && grupoCuenta!="" && nombreCuenta!=""){
		 $.post("/cuentasContables/modificar",{
			 "id":id,
			 "tipoCuenta":tipoCuenta,
			 "grupoCuenta":grupoCuenta,
			 "codigoCuenta":codigoCuenta,
			 "nombreCuenta":nombreCuenta,
			 "cuentaControl":cuentaControl,
			 "idCuentaControl":idCuentaControl
		 },function(data){
			 	if(data == "1"){
			 		$("#tablaCuentasContables").load("/cuentasContables/mostrarCuentasContables",function(data){
			 			console.log("Cuentas Contables");
			 			addEvents();
				 		$("#modalModificarCuentas").modal("hide");
						Swal.fire({
							  title: 'Muy Bien!',
							  text: "Registro modificado",
							  icon: 'success',
							  position : 'top',
							  showCancelButton: false,
							  confirmButtonColor: '#3085d6',
							  confirmButtonText: 'Ok!'
							}).then((result) => {
							  if (result.isConfirmed) {
								$("#modalCuentas").modal("show");
							  }
							})
			 		});
			 	}else if(data == "-1"){
			 		$("#modalModificarCuentas").modal("hide");
					Swal.fire({
						  title: 'Alerta!',
						  text: "No se puede agregar esta cuenta",
						  icon: 'warning',
						  position : 'top',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  confirmButtonText: 'Ok!'
						}).then((result) => {
						  if (result.isConfirmed) {
							$("#modalModificarCuentas").modal("show");
						  }
						})
			 	}else if(data == "-2"){
			 		$("#modalModificarCuentas").modal("hide");
					Swal.fire({
						  title: 'Alerta!',
						  text: "No puede agregar cuentas a cuentas auxiliares",
						  icon: 'warning',
						  position : 'top',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  confirmButtonText: 'Ok!'
						}).then((result) => {
						  if (result.isConfirmed) {
							$("#modalModificarCuentas").modal("show");
						  }
						})
			 	}else if(data == "-3"){
			 		$("#modalModificarCuentas").modal("hide");
			 		Swal.fire({
						  title: 'Alerta!',
						  text: "No puede modificar esta cuenta",
						  icon: 'warning',
						  position : 'top',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  confirmButtonText: 'Ok!'
						}).then((result) => {
						  if (result.isConfirmed) {
							$("#modalCuentas").modal("show");
						  }
						})
			 	}else{
			 		//No guardo
			 		$("#modalModificarCuentas").modal("hide");
					Swal.fire({
						  title: 'Alerta!',
						  text: "No se guardo la cuenta contable",
						  icon: 'warning',
						  position : 'top',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  confirmButtonText: 'Ok!'
						}).then((result) => {
						  if (result.isConfirmed) {
							$("#modalModificarCuentas").modal("show");
						  }
						})
			 	}
				addEvents();
		 });
	}else{
		$("#modalModificarCuentas").modal("hide");
		Swal.fire({
			  title: 'Alerta!',
			  text: "El nombre, el tipo y el Grupo cuenta no pueden estar vacio",
			  icon: 'warning',
			  position : 'top',
			  showCancelButton: false,
			  confirmButtonColor: '#3085d6',
			  confirmButtonText: 'Ok!'
			}).then((result) => {
			  if (result.isConfirmed) {
				$("#modalModificarCuentas").modal("show");
			  }
			})
	}
});
/******************************************************Fin Contabilidad **********************************************************/

/******************************************************** Usuarios **********************************************************/

$("#usuarios").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	styleSeccionAll();
	$("#contenido").load("/usuarios/listaUsuarios",function(data){
		console.log("Agregar Usuario");
		addEvents();
	});
});

$("#perfil").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	styleSeccionOriginal
	$("#contenido").load("/usuarios/cambiarClaveUsuarioAcct",function(data){
		addEvents();
	});
});

$("#listaUsuarios").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	$("#contenido").load("/usuarios/listaUsuarios",function(data){
		console.log("lista Usuario");
		addEvents();
	});
});

$("#agregarUsuarios").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	$("#contenido").load("/usuarios/agregarUsuarios",function(data){
		console.log("Agregar Usuario");
		addEvents();
	});
});

$("#formAgregarUsuario").submit(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var datos = $("#formAgregarUsuario").serializeArray();
	console.log(datos);
	 $.post("/usuarios/crear",datos,
		function(data){
			if(data == "1"){
//				$("#contenido").replaceWith(data);
				Swal.fire({
					title : 'Muy bien!',
					text : 'Usuario creado correctamente',
					position : 'top',
					icon : 'success',
					confirmButtonText : 'Cool'
				})
				addEvents();
			}else if(data == "2"){
				Swal.fire({
					title : 'Alerta!',
					text : 'El usuario ya existe',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}else if(data == "0"){
				Swal.fire({
					title : 'Alerta!',
					text : 'No se guardo el usuario',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}
			$("#contenido").load("/usuarios/agregarUsuarios",function(data){
				console.log("Agregar Usuario");
				addEvents();
			});
		});
});

$("#formModificarPasswordUsuarioAcct").submit(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var datos = $("#formModificarPasswordUsuarioAcct").serializeArray();
	console.log(datos);
	 $.post("/usuarios/modificarPasswordAcct",datos,
		function(data){
			if(data == "1"){
//				$("#contenido").replaceWith(data);
				Swal.fire({
					title : 'Muy bien!',
					text : 'Usuario modificado correctamente',
					position : 'top',
					icon : 'success',
					confirmButtonText : 'Cool'
				})
				addEvents();
			}else if(data == "2"){
				Swal.fire({
					title : 'Alerta!',
					text : 'El usuario ya existe',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}else if(data == "0"){
				Swal.fire({
					title : 'Alerta!',
					text : 'No se modifico el usuario',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}
			$("#contenido").load("/usuarios/cambiarClaveUsuarioAcct",function(data){
				addEvents();
			});
		});
});

$("#formModificarPasswordUsuario").submit(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var datos = $("#formModificarPasswordUsuario").serializeArray();
	console.log(datos);
	 $.post("/usuarios/modificarPassword",datos,
		function(data){
			if(data == "1"){
//				$("#contenido").replaceWith(data);
				Swal.fire({
					title : 'Muy bien!',
					text : 'Usuario modificado correctamente',
					position : 'top',
					icon : 'success',
					confirmButtonText : 'Cool'
				})
				addEvents();
			}else if(data == "2"){
				Swal.fire({
					title : 'Alerta!',
					text : 'El usuario ya existe',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}else if(data == "0"){
				Swal.fire({
					title : 'Alerta!',
					text : 'No se modifico el usuario',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}
			$("#contenido").load("/usuarios/agregarUsuarios",function(data){
				console.log("Agregar Usuario");
				addEvents();
			});
		});
});

$("#formModificarUsuario").submit(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var datos = $("#formModificarUsuario").serializeArray();
	console.log(datos);
	 $.post("/usuarios/modificar",datos,
		function(data){
			if(data == "1"){
//				$("#contenido").replaceWith(data);
				Swal.fire({
					title : 'Muy bien!',
					text : 'Usuario modificado correctamente',
					position : 'top',
					icon : 'success',
					confirmButtonText : 'Cool'
				})
				addEvents();
			}else if(data == "2"){
				Swal.fire({
					title : 'Alerta!',
					text : 'El usuario ya existe',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}else if(data == "0"){
				Swal.fire({
					title : 'Alerta!',
					text : 'No se modifico el usuario',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}
			$("#contenido").load("/usuarios/agregarUsuarios",function(data){
				console.log("Agregar Usuario");
				addEvents();
			});
		});
});
/******************************************************** Fin Usuarios **********************************************************/

/******************************************************** Cobros **********************************************************/

$("#cobros").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	ocultarDetalleAmortizacion();
	styleSeccionAll();
	$("#contenido").load("/prestamos/prestamosPendientes",function(data){
		console.log("Prestamos pendientes");
		addEvents();
	});
});

$("#btnCarpetaCobros").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var moneda = $("#selectMonedas").val();
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
		            url: "/carpetas/buscarCarpetaCobros/"+carpeta,
		            type: "GET",
//		            data: "",
//		            enctype: 'multipart/form-data',
		            processData: false,
		            contentType: false,
		            cache: false,
		            success: function (res) {
						$("#buscarCarpeta").replaceWith(res);
		            	var msg = $("#msgId").val();
			            if(msg=="0"){
							Swal.fire({
								title : 'No tienes carpetas creadas!',
//								text : 'No tienes carpetas creadas',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
							addEvents();
						}else{
							Swal.fire({
								title : 'Muy bien!',
								text : 'Carpeta Seleccionada',
								position : 'top',
								icon : 'success',
								confirmButtonText : 'Cool'
							})
		
							$("#tablaPrestamosCobros").load("/prestamos/prestamosPendientesFiltro",
								{
									"moneda":moneda
								},function(data){
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

$("#tipoPago").change(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var tipoPago = $("#tipoPago").val();
	if(tipoPago == "2" || tipoPago == "3"){
		 $("#imagenTransaccionAbono").show();
	}else{
		 $("#imagenTransaccionAbono").hide();
	}
});

$("#agregarPagoTemp").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var prestamoAcct = $("#prestamoAcct").val(); 
	$("#idPrestamoAbonoNota").val(prestamoAcct); 
	var form = $('#formAbonosNotas')[0];
	console.log();
	  $.ajax({
          url: "/prestamos/crearPagoTemp",
          type: "POST",
          data: new FormData(form),
          enctype: 'multipart/form-data',
          processData: false,
          contentType: false,
          cache: false,
          success: function (res) {
          	$("#tablaPagos").replaceWith(res);
	        addEvents();
	        montoTotalAbono();
	        $("#montoAbonoCuota").val("");
	        $("#archivoPago").val("");
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
          }});
   });

$("#selectEstadoCuota").change(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var estado = $("#selectEstadoCuota").val();
	var moneda = $("#selectMonedas").val();
	$("#tablaPrestamosCobros").load("/prestamos/prestamosPendientesFiltro",
		{
			"moneda": moneda,
			"estado": estado
		},function(data){
		console.log("Lista de prestamos del cliente");
		addEvents();
		var estadoCuotaHidden = $("#estadoCuotaHidden").val();
		$("#selectEstadoCuota option[value="+ estadoCuotaHidden +"]").attr("selected",true);
	});
});

$("#selectMonedas").change(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var estado = $("#selectEstadoCuota").val();
	var moneda = $("#selectMonedas").val();
	
	$("#tablaPrestamosCobros").load("/prestamos/prestamosPendientesFiltro",
		{
			"moneda": moneda,
			"estado": estado
		},function(data){
		console.log("Lista de prestamos del cliente");
		addEvents();
	});
});

$('#nombreOCedula').on('keyup', function(e) {
	e.preventDefault();
	e.stopImmediatePropagation();
	var item = $('#nombreOCedula').val();
	var estado = $("#selectEstadoCuota").val();
	var moneda = $("#selectMonedas").val();
	var longitud = item.length;
	if(longitud > 2){
		$("#tablaPrestamosCobros").load("/prestamos/prestamosPendientesFiltro",
			{
				"moneda": moneda,
				"estado": estado,
				"item": item
			},function(data){
			console.log("Lista de prestamos del cliente");
			addEvents();
		});
	}else{
		$("#tablaPrestamosCobros").load("/prestamos/prestamosPendientesFiltro",
				{
					"moneda": moneda,
					"estado": estado,
					"item": 0
				},function(data){
				console.log("Lista de prestamos del cliente");
				addEvents();
			});
	}
});

$("#btnExitCarpetaCobros").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var moneda = $("#selectMonedas").val();
	  $.ajax({
          url: "/carpetas/buscarCarpetaCobros",
          type: "GET",
//          data: "",
//          enctype: 'multipart/form-data',
          processData: false,
          contentType: false,
          cache: false,
          success: function (res) {
				$("#buscarCarpeta").replaceWith(res);
          	var msg = $("#msgId").val();
	            if(msg=="0"){
					addEvents();
					location.href = '/logout';
				}else{
					$("#tablaPrestamosCobros").load("/prestamos/prestamosPendientesFiltro",
						{
							"moneda":moneda
						},function(data){
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
});	

/******************************************************* Fin Cobros **********************************************************/

/******************************************************** Empleados *********************************************************/

$("#empleados").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	styleSeccionAll();
	$("#contenido").load("/empleados/listaEmpleados",function(data){
		ocultarDetalleAmortizacion();
		console.log("Lista de empleados");
		addEvents();
	});
});

$("#listaEmpleados").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#contenido").load("/empleados/listaEmpleados",function(data){
		ocultarDetalleAmortizacion();
		console.log("Lista de empleados");
		addEvents();
	});
});

$("#agregarEmpleado").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#contenido").load("/empleados/agregarEmpleado",function(data){
		ocultarDetalleAmortizacion();
		console.log("Agregar empleados");
		addEvents();
	});
});

$("#formEmpleado").submit(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var datos = $("#formEmpleado").serializeArray();
	
//	if(){
//		datos.push( {name:'id', value:id} );
//	}
	
	console.log(datos);
	 $.post("/empleados/guardar",datos,
		function(data){
			if(data == "1"){
//				$("#contenido").replaceWith(data);
				Swal.fire({
					title : 'Muy bien!',
					text : 'Empleado creado correctamente',
					position : 'top',
					icon : 'success',
					confirmButtonText : 'Cool'
				})
				addEvents();
			}else if(data == "2"){
				Swal.fire({
					title : 'Alerta!',
					text : 'El empleado ya existe',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}else if(data == "0"){
				Swal.fire({
					title : 'Alerta!',
					text : 'No se guardo el empleado',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}else if(data == "4"){
				Swal.fire({
					title : 'Muy bien!',
					text : 'Empleado modificado correctamente',
					position : 'top',
					icon : 'success',
					confirmButtonText : 'Cool'
				})
			}
			$("#contenido").load("/empleados/agregarEmpleado",function(data){
				console.log("Agregar Empleado");
				addEvents();
			});
		});
});

$("#agregarAsignacion").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var idEmpleado = $("#idEmpleadoAsignacion").val();
	var motivoAsignacion = $("#motivoAsignacion").val();
	var montoAsignacion = $("#montoAsignacion").val();
	
	if(!montoAsignacion || !motivoAsignacion){
		$("#modalAsignacion").modal("hide");
		Swal.fire({
			title : 'Alerta!',
			text : 'Los campos no pueden estar vacios',
			position : 'top',
			icon : 'warning',
			confirmButtonText : 'Cool'
		})
	}else{
		$("#asignacionesEmpleado").load("/empleados/guardarAsignacion",
				{
					"idEmpleado" : idEmpleado,
					"motivo" : motivoAsignacion,
					"monto" : montoAsignacion
				},function(data){
					$("#modalAsignacion").modal("hide");
					if(data == "0"){
						Swal.fire({
							title : 'Alerta!',
							text : 'No se guardo la asignacion',
							position : 'top',
							icon : 'warning',
							confirmButtonText : 'Cool'
						})
					}else{
						$("#modalAsignacion").modal("show");
						console.log("Guardar Asignacion");
						$("#asignacionesEmpleado").load("/empleados/listaAsignaciones/"+idEmpleado,function(data){
							$("#motivoAsignacion").val("");
							$("#montoAsignacion").val("");
							console.log("Agregar Asignacion");
						});
					}

		});
	}
});

$("#agregarDeduccion").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var idEmpleado = $("#idEmpleadoDeduccion").val();
	var motivoDeduccion = $("#motivoDeduccion").val();
	var montoDeduccion = $("#montoDeduccion").val();
	
	if(!montoDeduccion || !motivoDeduccion){
		$("#modalDeduccion").modal("hide");
		Swal.fire({
			title : 'Alerta!',
			text : 'Los campos no pueden estar vacios',
			position : 'top',
			icon : 'warning',
			confirmButtonText : 'Cool'
		})
	}else{
		$("#deduccionesEmpleado").load("/empleados/guardarDeduccion",
				{
					"idEmpleado" : idEmpleado,
					"motivo" : motivoDeduccion,
					"monto" : montoDeduccion
				},function(data){
					$("#modalDeduccion").modal("hide");
					if(data == "0"){
						Swal.fire({
							title : 'Alerta!',
							text : 'No se guardo la deduccion',
							position : 'top',
							icon : 'warning',
							confirmButtonText : 'Cool'
						})
					}else{
						$("#modalDeduccion").modal("show");
						console.log("Guardar deduccion");
						$("#deduccionesEmpleado").load("/empleados/listaDeducciones/"+idEmpleado,function(data){
							$("#motivoDeduccion").val("");
							$("#montoDeduccion").val("");
						});
					}

		});
	}
});

/******************************************************** Fin Empleados *****************************************************/

/******************************************************** Empresas **********************************************************/

$("#empresas").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	styleSeccionAll();
	$("#contenido").load("/empresas/listaEmpresas",function(data){
		ocultarDetalleAmortizacion();
		console.log("Lista de empresas");
		addEvents();
	});
});

$("#agregarEmpresa").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#contenido").load("/empresas/agregarEmpresa",function(data){
		ocultarDetalleAmortizacion();
		console.log("Formulario de empresas");
		addEvents();
	});
});

$("#formAddEmpresa").on("submit", function (e) {
	e.preventDefault();
	e.stopImmediatePropagation();
        $.ajax({
            url: "/empresas/crear/",
            type: "POST",
            data: new FormData(this),
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            cache: false,
            success: function (res) {
	            if(res=="1"){
					Swal.fire({
						title : 'Muy bien!',
						text : 'Registro guardado',
						position : 'top',
						icon : 'success',
						confirmButtonText : 'Cool'
					})
				}else{
					Swal.fire({
						title : 'Error!',
						text : 'No se pudo crear el registro',
						position : 'top',
						icon : 'error',
						confirmButtonText : 'Cool'
					})
				}
	               $("#contenido").load("/empresas/agregarEmpresa",function(data){
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

$("#formUpdateEmpresa").on("submit", function (e) {
	e.preventDefault();
	e.stopImmediatePropagation();
        $.ajax({
            url: "/empresas/modificar/",
            type: "POST",
            data: new FormData(this),
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            cache: false,
            success: function (res) {
	            if(res=="1"){
					Swal.fire({
						title : 'Muy bien!',
						text : 'Registro modificado',
						position : 'top',
						icon : 'success',
						confirmButtonText : 'Cool'
					})
				}else{
					Swal.fire({
						title : 'Error!',
						text : 'No se pudo modificar el registro',
						position : 'top',
						icon : 'error',
						confirmButtonText : 'Cool'
					})
				}
	               $("#contenido").load("/empresas/agregarEmpresa",function(data){
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

/******************************************************** Fin Empresas ******************************************************/

/******************************************************** Cuentas ***********************************************************/

$("#cuentas").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	styleSeccionAll();
	$("#contenido").load("/cuentas/listaCuentas",function(data){
		ocultarDetalleAmortizacion();
		console.log("Lista de cuentas");
		addEvents();
	});
});

$("#btnCarpetaCuentas").click(function(e){
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
		            url: "/carpetas/buscarCarpetaCuentas/"+carpeta,
		            type: "GET",
		            processData: false,
		            contentType: false,
		            cache: false,
		            success: function (res) {
						$("#buscarCarpeta").replaceWith(res);
		            	var msg = $("#msgId").val();
			            if(msg=="0"){
							Swal.fire({
								title : 'No tienes carpetas creadas!',
//								text : 'No tienes carpetas creadas',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
							addEvents();
						}else{
							Swal.fire({
								title : 'Muy bien!',
								text : 'Carpeta Seleccionada',
								position : 'top',
								icon : 'success',
								confirmButtonText : 'Cool'
							})
		
							$("#contenido").load("/cuentas/listaCuentas",function(data){
								ocultarDetalleAmortizacion();
								console.log("Lista de cuentas");
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

$("#btnExitCarpetaCuentas").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	  $.ajax({
          url: "/carpetas/buscarCarpetaCuentas",
          type: "GET",
          processData: false,
          contentType: false,
          cache: false,
          success: function (res) {
				$("#buscarCarpeta").replaceWith(res);
          	var msg = $("#msgId").val();
	            if(msg=="0"){
					addEvents();
					location.href = '/logout';
				}else{
					$("#contenido").load("/cuentas/listaCuentas",function(data){
						ocultarDetalleAmortizacion();
						console.log("Lista de cuentas");
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
});	

$("#agregarCuenta").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#contenido").load("/cuentas/agregarCuenta",function(data){
		ocultarDetalleAmortizacion();
		console.log("Formulario de cuentas");
		addEvents();
	});
});

$("#formAddCuenta").on("submit", function (e) {
	e.preventDefault();
	e.stopImmediatePropagation();
        $.ajax({
            url: "/cuentas/crear/",
            type: "POST",
            data: new FormData(this),
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            cache: false,
            success: function (res) {
	            if(res=="1"){
					Swal.fire({
						title : 'Muy bien!',
						text : 'Registro guardado',
						position : 'top',
						icon : 'success',
						confirmButtonText : 'Cool'
					})
				}else{
					Swal.fire({
						title : 'Error!',
						text : 'No se pudo crear el registro',
						position : 'top',
						icon : 'error',
						confirmButtonText : 'Cool'
					})
				}
	            $("#contenido").load("/cuentas/agregarCuenta",function(data){
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

$("#formUpdateCuenta").on("submit", function (e) {
	e.preventDefault();
	e.stopImmediatePropagation();
        $.ajax({
            url: "/cuentas/modificar/",
            type: "POST",
            data: new FormData(this),
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            cache: false,
            success: function (res) {
	            if(res=="1"){
					Swal.fire({
						title : 'Muy bien!',
						text : 'Registro modificado',
						position : 'top',
						icon : 'success',
						confirmButtonText : 'Cool'
					})
				}else{
					Swal.fire({
						title : 'Error!',
						text : 'No se pudo modificar el registro',
						position : 'top',
						icon : 'error',
						confirmButtonText : 'Cool'
					})
				}
	            $("#contenido").load("/cuentas/agregarCuenta",function(data){
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

/******************************************************** Fin Cuentas *******************************************************/

/******************************************************** Reportes **********************************************************/

$("#reportes").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	styleSeccionAll();
	$("#contenido").load("/reportes/listaReportes",function(data){
		ocultarDetalleAmortizacion();
		console.log("Lista de Reportes");
		addEvents();
	});
});

$("#btnCarpetaReportes").click(function(e){
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
		            url: "/carpetas/buscarCarpetaReportes/"+carpeta,
		            type: "GET",
		            processData: false,
		            contentType: false,
		            cache: false,
		            success: function (res) {
						$("#buscarCarpeta").replaceWith(res);
		            	var msg = $("#msgId").val();
			            if(msg=="0"){
							Swal.fire({
								title : 'No tienes carpetas creadas!',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
							addEvents();
						}else{
							Swal.fire({
								title : 'Muy bien!',
								text : 'Carpeta Seleccionada',
								position : 'top',
								icon : 'success',
								confirmButtonText : 'Cool'
							})
		
							$("#contenido").load("/reportes/listaReportes",function(data){
								ocultarDetalleAmortizacion();
								console.log("Lista de Reportes");
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

$("#btnExitCarpetaReportes").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	  $.ajax({
          url: "/carpetas/buscarCarpetaReportes",
          type: "GET",
          processData: false,
          contentType: false,
          cache: false,
          success: function (res) {
				$("#buscarCarpeta").replaceWith(res);
          	var msg = $("#msgId").val();
	            if(msg=="0"){
					addEvents();
					location.href = '/logout';
				}else{
					$("#contenido").load("/reportes/listaReportes",function(data){
						ocultarDetalleAmortizacion();
						console.log("Lista de Reportes");
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
});	

$("#btnMostrarPrestamoImpresion").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#modalImpresionPrestamo").modal("show");
	addEvents();
});

$("#btnMostrarDescuentosImpresion").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#modalImpresionDescuentos").modal("show");
	addEvents();
});

$("#btnBuscarDescuentoImpresion").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var desde = $("#DescuentoDesdeImpresion").val();
	var hasta = $("#DescuentoHastaImpresion").val();
	
	if(desde && hasta){
		$("#modalImpresionDescuentos").modal("hide");
		$("#cuerpoImpresion").load("/reportes/listaDescuentos/"+desde+"/"+hasta,function(data){
			addEvents();
		});
	}
});

$("#imprimirListaDescuentos").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	 $.get("/reportes/listaDescuentos",function(elemento){
		impresion(elemento);
		addEvents();
	 });
});

$("#btnMostrarPorCobrarImpresion").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();

	$("#cuerpoImpresion").load("/reportes/porCobrar",function(data){
		addEvents();
	});

});

$("#imprimirListaPendientes").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	 $.get("/reportes/listaPendientes",function(elemento){
		impresion(elemento);
		addEvents();
	 });
});

$("#btnBuscarPrestamoImpresion").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var desde = $("#PrestamoDesdeImpresion").val();
	var hasta = $("#PrestamoHastaImpresion").val();
	
	if(desde && hasta){
		$("#modalImpresionPrestamo").modal("hide");
		$("#cuerpoImpresion").load("/reportes/listaPrestamos/"+desde+"/"+hasta,function(data){
			addEvents();
		});
	}
});

$("#imprimirListaPrestamos").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	 $.get("/reportes/listaPrestamos",function(elemento){
		impresion(elemento);
		addEvents();
	 });
});

$("#btnMostrarAbonoImpresion").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$("#modalImpresionAbono").modal("show");
	addEvents();
});

$("#btnBuscarAbonosImpresion").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var desde = $("#AbonoDesdeImpresion").val();
	var hasta = $("#AbonoHastaImpresion").val();
	var idCliente = $("#clienteAbonoImpresion").val();
	
	if(!desde){
		desde = "";
	}
	
	if(!hasta){
		hasta = "";
	}
	
	$("#modalImpresionAbono").modal("hide");
	$("#cuerpoImpresion").load("/reportes/listaAbonos",
		{
			'desde': desde,
			'hasta': hasta,
			'idCliente': idCliente
		},
	function(elemento){
		$("#encabezadoAbono").hide();
		impresion(elemento);
		addEvents();
	});		
});

/******************************************************** Fin Reportes ******************************************************/

/******************************************************** Administracion ****************************************************/

$("#administracion").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	styleSeccionAll();
	$("#contenido").load("/administracion/",function(data){
		ocultarDetalleAmortizacion();
		console.log("Administracion");
		addEvents();
	});
});

$("#crearToken").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	$.get("/administracion/crearToken",
		function(data){
		Swal.fire({
			title : 'Muy bien!',
			text : 'Token Generado: '+data,
			position : 'top',
			icon : 'success',
			confirmButtonText : 'Cool'
		})
		$("#contenido").load("/administracion/",function(data){
			ocultarDetalleAmortizacion();
			console.log("Administracion");
			addEvents();
		});
	});
});

$("#actualizarToken").click(function(e){
	e.preventDefault();
	e.stopImmediatePropagation();
	var tokenId = $("#tokenId").val();
	var expire = $("#fechaExpiracion").val();
	var estado = $("#estadoToken").val();
	
	if(!expire){
		expire = "";
	}
	
	$.post("/administracion/actualizarToken",{
		 "tokenId":tokenId,
		 "expire":expire,
		 "estado":estado
	 },function(data){
		 $("#modalToken").modal("hide");
		 if(data == "1"){
			 Swal.fire({
					title : 'Muy bien!',
					text : 'Token Actualizado',
					position : 'top',
					icon : 'success',
					confirmButtonText : 'Cool'
				})
		 }else{
			 Swal.fire({
					title : 'Alerta!',
					text : 'No se pudo actualizar el token',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
		 }
		 $("#contenido").load("/administracion/",function(data){
				ocultarDetalleAmortizacion();
				console.log("Administracion");
				addEvents();
		 });
	 });
});

/******************************************************** Fin Administracion ****************************************************/
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
		 if($("#doctypeTemp").val() == "cedula"){
        	 $('#cedulaCliente').attr('maxlength', 13);
        	 $("#docCedula").prop("checked", true);
		 }else{
			 $('#cedulaCliente').attr('maxlength', 50);
			 $("#docPasaporte").prop("checked", true);
		 }
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
				if($("#tipoPrestamo").val()=='2'){
					//Interes
					$("#thMontoCuota").hide();
					$("#thMontoCapital").hide();
					$("#thInteresGenerado").text("Int/Gen");
					$("#thInteres").text("Int/Hoy");
					$("#thInteresGenerado").show();
				}else{
					//Cuotas
					$("#thMontoCuota").show();
					$("#thMontoCapital").show();
					$("#thInteres").text("Interes");
					$("#thInteresGenerado").hide();
				}
			$("#botonesAccionPrestamo").show();
			 $.get("/prestamos/getTipoPrestamo/"+id,
					function(data){
				 		if(data == "0"){
				 			Swal.fire({
								title : 'Alerta!',
								text : 'No se encontro la informacion del tipo de prestamo',
								position : 'top',
								icon : 'warning',
								confirmButtonText : 'Cool'
							})
				 		}else{
				 			if(data == "vehiculo"){
					 			$("#btnAddDataVehiculo").show();
				 			}else{
				 				$("#btnAddDataVehiculo").hide();
				 			}
				 		}
			 		});
				addEvents();
	 	});
}

function checkCuota(idDetalle){
	var idPrestamo = $("#prestamoAcct").val();
	var tipoCuota = $("#selectTipoCuotaAbono").val();
	var selected = '';    
    $('.checkCuota').each(function(){
        if (this.checked) {
            selected += $(this).val()+', ';
        }
    }); 

    if (selected == '') {
    	$("#totalMora").text("");
    	$("#totalCargo").text("");
    	$("#totalDescuento").text("");
    	$("#totalBalance").text("");
    }else{
    	//Cargar precios totales
		 $.post("/prestamos/totalesCuotas/",{
			 "idPrestamo":idPrestamo,
			 "cuotas":selected,
			 "tipoCuota":tipoCuota
		 },function(data){
				console.log("Montos totales Cutotas");
				$("#totalesGeneralesPrestamo").replaceWith(data);
				addEvents();
		 });
    }
}

function cargarDetalleCargo(idPrestamoInteresDetalle){
	var idPrestamo = $("#prestamoAcct").val();
	$("#tablaCargos").load("/prestamos/detallesCargos/"+idPrestamoInteresDetalle+"/"+idPrestamo,function(data){
		console.log("Detalles de los cargos");
		$("#modalDetalleCargos").modal("show");
	});
}

function cargarDetalleMora(idPrestamoInteresDetalle){
	var idPrestamo = $("#prestamoAcct").val();
	$("#tablaMoras").load("/prestamos/detallesMoras/"+idPrestamoInteresDetalle+"/"+idPrestamo,function(data){
		console.log("Detalles de las moras");
		$("#modalDetalleMoras").modal("show");
	});
}

function cargarDetalleCapital(idPrestamoInteresDetalle){
	var idPrestamo = $("#prestamoAcct").val();
	$("#capitalInfo").load("/prestamos/detallesCapitales/"+idPrestamoInteresDetalle+"/"+idPrestamo,function(data){
		console.log("Detalles de los capitales");
		$("#modalDetalleCapitales").modal("show");
	});
}

function cargarDetalleInteres(idPrestamoInteresDetalle){
	var idPrestamo = $("#prestamoAcct").val();
	$("#interesInfo").load("/prestamos/detallesIntereses/"+idPrestamoInteresDetalle+"/"+idPrestamo,function(data){
		console.log("Detalles de los Intereses");
		$("#modalDetalleIntereses").modal("show");
	});
}

function versubcat(id){
    var textdato = $("#"+id+"").text();

    if(textdato!='-'){
        $("#"+id+"").text("-");
        $("#subcategoria-"+id+"").show();
        $("#"+id+"").removeClass("btn btn-sm btn-info");
        $("#"+id+"").addClass("btn btn-sm btn-danger");
    }else{
        $("#"+id+"").text("+");
        $("#subcategoria-"+id+"").hide();
        $("#"+id+"").removeClass("btn btn-sm btn-danger");
        $("#"+id+"").addClass("btn btn-sm btn-info");
    }
}

function versubcat2(id){
    var textdato = $("#"+id+"").text();

    if(textdato!='-'){
        $("#"+id+"").text("-");
        $("#subcategoria2-"+id+"").show();
        $("#"+id+"").removeClass("btn btn-sm btn-info");
        $("#"+id+"").addClass("btn btn-sm btn-danger");
    }else{
        $("#"+id+"").text("+");
        $("#subcategoria2-"+id+"").hide();
        $("#"+id+"").removeClass("btn btn-sm btn-danger");
        $("#"+id+"").addClass("btn btn-sm btn-info");
    }
}

function versubcat3(id){
    var textdato = $("#"+id+"").text();

    if(textdato!='-'){
        $("#"+id+"").text("-");
        $("#subcategoria3-"+id+"").show();
        $("#"+id+"").removeClass("btn btn-sm btn-info");
        $("#"+id+"").addClass("btn btn-sm btn-danger");
    }else{
        $("#"+id+"").text("+");
        $("#subcategoria3-"+id+"").hide();
        $("#"+id+"").removeClass("btn btn-sm btn-danger");
        $("#"+id+"").addClass("btn btn-sm btn-info");
    }
}

function versubcat4(id){
    var textdato = $("#"+id+"").text();

    if(textdato!='-'){
        $("#"+id+"").text("-");
        $("#subcategoria4-"+id+"").show();
        $("#"+id+"").removeClass("btn btn-sm btn-info");
        $("#"+id+"").addClass("btn btn-sm btn-danger");
    }else{
        $("#"+id+"").text("+");
        $("#subcategoria4-"+id+"").hide();
        $("#"+id+"").removeClass("btn btn-sm btn-danger");
        $("#"+id+"").addClass("btn btn-sm btn-info");
    }
}

function modificarUsuario(id){
	$("#contenido").load("/usuarios/modificarUsuario/"+id,function(data){
		console.log("Formulario Modificar Usuario");
		addEvents();
	});
}

function cambiarPassword(id){
	$("#contenido").load("/usuarios/cambiarPassword/"+id,function(data){
		console.log("Formulario Cambiar Password");
		addEvents();
	});
}

function verNotas(id){
	$("#contenido").load("/prestamos/verNotas/"+id,function(data){
		console.log("Ver Notas");
		var idPrestamo = $("#prestamoAcct").val();
		addEvents();
		cargarDetallePrestamo(idPrestamo);
	});
}

function eliminarPagoComprasTemp(id){
	var idCompra = $("#idCompraCxP").val();
	$.get("/compras/eliminarPagoTemp/"+id, function(data){
		$("#tablaPagosCompraTemp").load("/compras/pagosTemporales/"+idCompra,function(data){
			addEvents();
		});
	});
}

function eliminarPagoTemp(id){
	$("#tablaPagos").load("/prestamos/eliminarPagoTemp/"+id, function(data){
		montoTotalAbono();
		addEvents();
	});
}

function montoTotalAbono(){
	var idPrestamo = $("#prestamoAcct").val();
	$("#montoTotalAbono").load("/prestamos/montoTotalAbono/"+idPrestamo, function(data){
		addEvents();
	});
}

function verInfoNotas(id){
	$("#tablaNotas").load("/notas/listaNotas/"+id,function(data){
		console.log("Ver Notas");
		$("#modalDetalleNotas").modal("show");
		addEvents();
	});
}

function imprimirAbonoDetalle(id){
	window.open("/prestamos/imprimirDetalleAbono/"+id);
}

function verEmpleado(id){
	$("#datosEmpleado").load("/empleados/infoEmpleado/"+id,function(data){
		$("#modalEmpleado").modal("show");
	});
}

function modificarEmpleado(id){
	$("#contenido").load("/empleados/modificarEmpleado/"+id,function(data){
		console.log("Modificar Empleado");
		addEvents();
	});
}

function asignarDeduccion(id){
	$("#deduccionesEmpleado").load("/empleados/listaDeducciones/"+id,function(data){
		$("#idEmpleadoDeduccion").val(id);
		$("#motivoDeduccion").val("");
		$("#montoDeduccion").val("");
		$("#modalDeduccion").modal("show");
		console.log("Agregar Deduccion");
	});
}

function asignarAsignacion(id){
	$("#asignacionesEmpleado").load("/empleados/listaAsignaciones/"+id,function(data){
		$("#idEmpleadoAsignacion").val(id);
		$("#motivoAsignacion").val("");
		$("#montoAsignacion").val("");
		$("#modalAsignacion").modal("show");
		console.log("Agregar Asignacion");
	});
}

function eliminarAsignacion(id){
	$("#modalAsignacion").modal("hide");
	
	Swal.fire({
		  title: 'Seguro de eliminar el registro?',
		  icon: 'warning',
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  cancelButtonText: 'Cancelar',
		  position : 'top',
		  confirmButtonText: 'Si, Eliminar!'
		}).then((result) => {
		  if (result.isConfirmed) {
				$("#modalAsignacion").modal("hide");
				$("#asignacionesEmpleado").load("/empleados/eliminarAsignacion/"+id,function(data){
					$("#motivoAsignacion").val("");
					$("#montoAsignacion").val("");
					console.log("Eliminar Asignacion");
				}); 
				Swal.fire({
					title : 'Borrado!',
					text : 'Se elimindo el registro',
					position : 'top',
					icon : 'success',
					confirmButtonText : 'Cool'
				})
		  }
		})
}

function eliminarDeduccion(id){
	$("#modalDeduccion").modal("hide");
	
	Swal.fire({
		  title: 'Seguro de eliminar el registro?',
		  icon: 'warning',
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  cancelButtonText: 'Cancelar',
		  position : 'top',
		  confirmButtonText: 'Si, Eliminar!'
		}).then((result) => {
		  if (result.isConfirmed) {
				$("#modalDeduccion").modal("hide");
				$("#deduccionesEmpleado").load("/empleados/eliminarDeduccion/"+id,function(data){
					$("#motivoDeduccion").val("");
					$("#montoDeduccion").val("");
					console.log("Eliminar Deduccion");
				}); 
				Swal.fire({
					title : 'Borrado!',
					text : 'Se elimindo el registro',
					position : 'top',
					icon : 'success',
					confirmButtonText : 'Cool'
				})
		  }
		})
}

function selectProductInList(id){
	$("#miniInfoProducto").load("/productos/miniInfoProduct/"+id,function(data){
		$("#idSelectedProduct").val(id);
		addEvents();
	});
}

function selectCompraInList(id){
	$("#miniInfoCompra").load("/compras/miniInfoCompra/"+id,function(data){
		$("#idSelectedCompra").val(id);
		addEvents();
	});
}

function modificarEmpresa(id){
	$("#contenido").load("/empresas/modificarEmpresa/"+id,function(data){
		ocultarDetalleAmortizacion();
		console.log("Formulario modificar empresas");
		addEvents();
	});
}

function modificarCuenta(id){
	$("#contenido").load("/cuentas/modificarCuenta/"+id,function(data){
		ocultarDetalleAmortizacion();
		console.log("Formulario modificar cuentas");
		addEvents();
	});
}

function modificarCuentaContable(id){
	$("#modalCuentas").modal('hide');
	$("#editCuentaContable").load("/cuentasContables/modificarCuentaContable/"+id,function(data){
		$("#modalModificarCuentas").modal('show');
		addEvents();
	});
}

function eliminarCuentaContable(id){
	$("#modalCuentas").modal('hide');
	$("#modalModificarCuentas").modal('hide');
	Swal.fire({
		  title: 'Seguro de eliminar el registro?',
		  icon: 'warning',
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  cancelButtonText: 'Cancelar',
		  position : 'top',
		  confirmButtonText: 'Si, Eliminar!'
		}).then((result) => {
		  if (result.isConfirmed) {
				 $.post("/cuentasContables/eliminar/",
						 {
							 "idCuentaContable":id
						 },function(data){
							 if(data == 1){
								 $("#modalCuentas").modal('hide');
									Swal.fire({
									title: 'Muy bien!',
									text: "Registro eliminado",
									icon: 'success',
									position : 'top',
									showCancelButton: false,
									 confirmButtonColor: '#3085d6',
									  confirmButtonText: 'Ok!'
									}).then((result) => {
									  if (result.isConfirmed) {
										$("#tablaCuentasContables").load("/cuentasContables/mostrarCuentasContables",function(data){
									 			console.log("Cuentas Contables");
									 			addEvents();
												$("#modalCuentas").modal("show");
									 	});
									  }
									})
							 }else{
								$("#modalCuentas").modal('hide');
								$("#modalModificarCuentas").modal('hide');
								Swal.fire({
								title: 'Alerta!',
								text: "No se pudo eliminar el registro",
								icon: 'warning',
								position : 'top',
								showCancelButton: false,
								 confirmButtonColor: '#3085d6',
								  confirmButtonText: 'Ok!'
								}).then((result) => {
								  if (result.isConfirmed) {
									$("#modalCuentas").modal("show");
								  }
							})
						 }
				});
		  }else{
			  $("#modalCuentas").modal("show");
		  }
		})
}

function modificarToken(idToken){
	$("#editToken").load("/administracion/modificarToken/"+idToken,function(data){
		$("#modalToken").modal('show');
		addEvents();
	});
}

function eliminarToken(tokenId){
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
		    
			 $.post("/administracion/eliminarToken",
					 {
						 "tokenId":tokenId
					},function(data){
						 if(data == "1"){
								Swal.fire({
									title: 'Muy bien!',
									text: "Registro eliminado",
									icon: 'success',
									position : 'top',
									showCancelButton: false,
									 confirmButtonColor: '#3085d6',
									  confirmButtonText: 'Ok!'
									}).then((result) => {
									  if (result.isConfirmed) {
										  $("#contenido").load("/administracion/",function(data){
												console.log("Administracion");
												addEvents();
											});
									  }
							})
						 }else{
							 Swal.fire({
								  icon: 'error',
								  title: 'Error!',
								  text: 'No se pudo borrar el registro!',
								  position: 'top'
								})
						 }
					});		
		  }
		})
}

function verCedula(idCliente){
	$("#detalleCedulaCliente").load("/prestamos/cedulaClienteCobros/"+idCliente,function(data){
		$("#modalVerCedulaCliente").modal("show");
		addEvents();
	});
}

function eliminarEntradaDiarioTemp(id){
	$("#tablaEntradasTemp").load("/contabilidad/eliminarEntradasDiariosTemp/"+id,function(data){
		addEvents();
	});
}

function eliminarProducto(id){
	$("#modalProductos").modal("hide");
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
		    
			 $.post("/productos/eliminarProducto",
					 {
						 "idProducto":id
					},function(data){
						 if(data == "1"){
								Swal.fire({
									title: 'Muy bien!',
									text: "Registro eliminado",
									icon: 'success',
									position : 'top',
									showCancelButton: false,
									 confirmButtonColor: '#3085d6',
									  confirmButtonText: 'Ok!'
									}).then((result) => {
									  if (result.isConfirmed) {
										  $("#tablaProductos").load("/productos/listaProductos",function(data){
												console.log("lista productos");
												$("#modalProductos").modal("show");
												addEvents();
											});
									  }
							})
						 }else{
							 Swal.fire({
								  icon: 'error',
								  title: 'Error!',
								  text: 'No se pudo borrar el registro!',
								  position: 'top'
								})
						 }
					});		
		  }else{
			  $("#tablaProductos").load("/productos/listaProductos",function(data){
					console.log("lista productos");
					$("#modalProductos").modal("show");
					addEvents();
				});
		  }
		})
}

function eliminarCuentaSuplidorTemp(id){
	$("#tablaCuentasTempSuplidor").load("/contabilidad/eliminarCuentaTempSuplidor/"+id,function(data){
		addEvents();
	});
}

function openModalImage(idProducto){
	 $("#imagenProducto").load("/productos/getImagen/"+idProducto,function(data){
		console.log("Imagen del producto");
		$("#modalImageProduct").modal("show");
		addEvents();
	});
}

function eliminarProductoTemp(id){
	$("#tablaCompras").load("/productos/deleteProductTemp/"+id,function(data){
		var suplidor = $("#selectSuplidorForProduct").val();
		if(suplidor != ""){
			$("#cuentasDelSuplidor").show();
			$("#cuentasDelSuplidor").load("/suplidores/cuentasDelSuplidor/"+suplidor,
				function(data){
					$.get("/compras/totalesCompras/"+suplidor,
						function(data){
							$("#subTotalCompra").val(data[0]);
							$("#totalPreCompra").val(data[1]);
							addEvents();
						});
				});
		}else{
			$("#cuentasDelSuplidor").hide();
		}
	});
}

function eliminarEnlace(id){
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
			 $.post("/contabilidad/borrarEnlace",
					 {
						 "id":id
					},function(data){
						 if(data == "1"){
								Swal.fire({
									title: 'Muy bien!',
									text: "Registro eliminado",
									icon: 'success',
									position : 'top',
									showCancelButton: false,
									 confirmButtonColor: '#3085d6',
									  confirmButtonText: 'Ok!'
									}).then((result) => {
									  if (result.isConfirmed) {
										  $("#tablaEnlacesItbis").load("/contabilidad/mostrarEnlacesItbis",function(data){
												$("#tablaEnlaces").load("/contabilidad/mostrarEnlaces",function(data){
													$("#tablaEnlacesProcesos").load("/contabilidad/mostrarEnlacesProcesos",function(data){
														$("#tablaEnlacesRetencion").load("/contabilidad/mostrarEnlacesRetencion",function(data){
															$("#tablaEnlacesEntidadEnlace").load("/contabilidad/mostrarEnlacesEntidadEnlace",function(data){
																addEvents();
															});
														});
													});
												});
											});
									  }
							})
						 }else{
							 Swal.fire({
								  icon: 'error',
								  title: 'Error!',
								  text: 'No se pudo borrar el registro!',
								  position: 'top'
								})
						 }
					});		
		  }
		})
}

function previousPageProduct(item){
	var page = "page="+item;
	var nombreProducto = $("#findByNombreProducto").val();
	var tipo = $("#findByTipoProducto").val();
	$("#tablaProductos").load("/productos/listaProductosFragment?"+page,
		{
			"nombre": nombreProducto,
			"tipo": tipo
		},function(data){
			$("#nombreInfoPoduct").val("");
          	 $("#costoInfoProduct").val(""); 
          	 $("#precioVentaInfoProduct").val("");
          	 $("#cantidadInfoProduct").val(""); 
         	 $("#tipoActivoInfoProduct").val("");
        	 $("#idSelectedProduct").val("");
			addEvents();
	});
}

function nextPageProduct(item){
	var page = "page="+item;
	var nombreProducto = $("#findByNombreProducto").val();
	var tipo = $("#findByTipoProducto").val();
	$("#tablaProductos").load("/productos/listaProductosFragment?"+page,
		{
			"nombre": nombreProducto,
			"tipo": tipo
		},function(data){
			$("#nombreInfoPoduct").val("");
          	 $("#costoInfoProduct").val(""); 
          	 $("#precioVentaInfoProduct").val("");
          	 $("#cantidadInfoProduct").val(""); 
         	 $("#tipoActivoInfoProduct").val("");
        	 $("#idSelectedProduct").val("");
			addEvents();
	});
}

function styleSeccionAll(){
	$("#cuerpo").css("min-height", "100%");
	$("#cuerpo").css("overflow", "hidden");
	$("#cuerpo").css("height", "100%");
	$("#cuerpo").css("max-height", "100%");
	$("#cuerpo").css("width", "100%");
	
	$("#contenido").css("min-height", "100%");
	$("#contenido").css("overflow", "hidden");
	$("#contenido").css("height", "100%");
	$("#contenido").css("max-height", "100%");
	$("#contenido").css("width", "100%");
	
	$("#formularioCuerpo").css("min-height", "100%");
	$("#formularioCuerpo").css("overflow", "hidden");
	$("#formularioCuerpo").css("height", "100%");
	$("#formularioCuerpo").css("max-height", "100%");
	$("#formularioCuerpo").css("width", "100%");
}

function styleSeccionOriginal(){
	$("#cuerpo").css("min-height", "420px");
	$("#cuerpo").css("overflow", "auto");
	$("#cuerpo").css("height", "420px");
	$("#cuerpo").css("max-height", "420px");
	
	$("#contenido").css("min-height", "400px");
	$("#contenido").css("overflow", "auto");
	$("#contenido").css("height", "400px");
	$("#contenido").css("max-height", "400px");
	
	$("#formularioCuerpo").css("min-height", "400px");
	$("#formularioCuerpo").css("overflow", "hidden");
	$("#formularioCuerpo").css("height", "400px");
	$("#formularioCuerpo").css("max-height", "400px");
	
}

function mostrararHerencia(idCuentaContable){
	$.get("/contabilidad/mostrarHerencia/"+idCuentaContable,
		function(data){
			addEvents();
	});
}

function mostrarDetalleAmortizacion(){
	$("#cuerpoTabla").show();
}

function ocultarDetalleAmortizacion(){
	$("#cuerpoTabla").hide();
}

function impresion(elemento){
	 var ventimp = window.open(' ', "MsgWindow", "width=1000,height=800");
	 ventimp.document.write('<html><head>');
//	 ventimp.document.write('<style>.table{width:100%;border-collapse:collapse;margin:16px 0 16px 0;}.table th{border:1px solid #ddd;padding:4px;background-color:#d4eefd;text-align:left;font-size:15px;}.table td{border:1px solid #ddd;text-align:left;padding:6px;}</style>');
	 ventimp.document.write('</head><body >');
	 ventimp.document.write(elemento);
	 ventimp.document.write('</body></html>');
	 ventimp.document.close();
	 ventimp.focus();
	 setTimeout(function() {
		 ventimp.print();
		}, 1000);
  }

function impresion2(elemento){
    var mywindow = window.open('', 'MsgWindow', 'height=1000,width=800');
    mywindow.document.write('<html><head>');
	mywindow.document.write('<style>.table{width:100%;border-collapse:collapse;margin:16px 0 16px 0;}.table th{border:1px solid #ddd;padding:4px;background-color:#d4eefd;text-align:left;font-size:15px;}.table td{border:1px solid #ddd;text-align:left;padding:6px;}</style>');
    mywindow.document.write('</head><body >');
    mywindow.document.write(document.getElementById(elemento).innerHTML);
    mywindow.document.write('</body></html>');
    mywindow.document.close(); // necesario para IE >= 10
    mywindow.focus(); // necesario para IE >= 10
    mywindow.print();
    mywindow.close();
    return true;
  }

function impresionTabla(elemento){
    var mywindow = window.open('', 'MsgWindow', 'height=1000,width=800');
    mywindow.document.write('<!DOCTYPE html>');
    mywindow.document.write('<html xmlns:th="http://www.thymeleaf.org"><head>');
	mywindow.document.write('<style>.table{width:100%;border-collapse:collapse;margin:16px 0 16px 0;}.table th{border:1px solid #ddd;padding:4px;background-color:#d4eefd;text-align:left;font-size:15px;}.table td{border:1px solid #ddd;text-align:left;padding:6px;}</style>');
    mywindow.document.write('</head><body>');
    mywindow.document.write('<p th:text="Hola"></p>');
    mywindow.document.write('<table class="table">');
    mywindow.document.write(document.getElementById(elemento).innerHTML);
    mywindow.document.write('</table>');
    mywindow.document.write('</body></html>');
    mywindow.document.close(); // necesario para IE >= 10
    mywindow.focus(); // necesario para IE >= 10
    mywindow.print();
    mywindow.close();
    return true;
  }
/******************************************************* Fin Funciones ******************************************************/
