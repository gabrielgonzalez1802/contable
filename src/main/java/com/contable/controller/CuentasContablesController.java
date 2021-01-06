package com.contable.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.model.Carpeta;
import com.contable.model.CuentaContable;
import com.contable.model.CuentaEnlace;
import com.contable.model.Empresa;
import com.contable.model.EntradaDiario;
import com.contable.model.EntradaIngresoContable;
import com.contable.model.Producto;
import com.contable.model.Usuario;
import com.contable.service.ICarpetasService;
import com.contable.service.ICuentasContablesService;
import com.contable.service.ICuentasEnlacesService;
import com.contable.service.IEntradasDiariosService;
import com.contable.service.IEntradasIngresosContableService;
import com.contable.service.IProductosService;

@Controller
@RequestMapping("/cuentasContables")
public class CuentasContablesController {
	
	@Autowired
	private ICuentasContablesService serviceCuentasContables;
	
	@Autowired
	private IEntradasDiariosService serviceEntradasDiarios;
	
	@Autowired
	private ICarpetasService serviceCarpetas;
	
	@Autowired
	private ICuentasEnlacesService serviceCuentasEnlaces;
	
	@Autowired
	private IProductosService serviceProductos;
	
	@Autowired
	private IEntradasIngresosContableService serviceEntradasIngresosContables;
	
	private List<CuentaContable> orderCuentaContableTemp;
	
	@GetMapping("/listaCuentasContables")
	public String listaCuentasContables(Model model, HttpSession session){
		orderCuentaContableTemp = new LinkedList<>();
		
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Carpeta carpeta = null;
		
		if(session.getAttribute("carpeta") != null) {
			 carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, empresa);
			if(!carpetas.isEmpty()) {
				carpeta = carpetas.get(0);
			}
		}
		
		List<CuentaContable> cuentasContables = serviceCuentasContables.buscarPorEmpresaOrderByCodigoDesc(empresa);

		for (CuentaContable cuentaContable : cuentasContables) {
			List<EntradaDiario> entradasDiarios = serviceEntradasDiarios.buscarPorCuentaContableEmpresaCarpeta(cuentaContable, empresa, carpeta);
			for (EntradaDiario entradaDiario : entradasDiarios) {
				BigDecimal monto = entradaDiario.getCredito().subtract(entradaDiario.getDebito());
				cuentaContable.setMonto(monto.doubleValue());
			}
			
			if(cuentaContable.getTipo().equals("C")) {
				List<CuentaContable> cuentasContablesTemp = serviceCuentasContables.
							buscarPorEmpresaCuentaControl(empresa, 
									cuentaContable.getCodigo());
				
				if(!cuentasContablesTemp.isEmpty()) {
					Double montoTemp = 0.0;
					for (CuentaContable cuentaContableTemp : cuentasContablesTemp) {
						List<EntradaDiario> entradasDiariosTemp = serviceEntradasDiarios.buscarPorCuentaContableEmpresaCarpeta(cuentaContableTemp, empresa, carpeta);
						for (EntradaDiario entradaDiarioTemp : entradasDiariosTemp) {
							montoTemp+=entradaDiarioTemp.getCredito().subtract(entradaDiarioTemp.getDebito()).doubleValue();
						}
						cuentaContable.setMonto(montoTemp);
					}
				}
			}
		}	
		
		for (CuentaContable cuentaContable2 : cuentasContables) {
			List<EntradaDiario> entradasDiarios = serviceEntradasDiarios.buscarPorCuentaContableEmpresaCarpeta(cuentaContable2, empresa, carpeta);
			double valorInicial = 0.0;
			for (EntradaDiario entradaDiario : entradasDiarios) {
					valorInicial += entradaDiario.getCredito().doubleValue()-entradaDiario.getDebito().doubleValue();
			}
				
			cuentaContable2.setMonto(valorInicial);
			
			//Toma el valor de los productos que coincida la cuenta contable (Compras)
			List<EntradaIngresoContable> entradasIngresosContables = serviceEntradasIngresosContables.buscarPorEmpresaCuentaContable(empresa, cuentaContable2);
			double costo = 0;
			for (EntradaIngresoContable ingresosContables : entradasIngresosContables) {
				costo += ingresosContables.getBalance();
			}
			cuentaContable2.setMonto(cuentaContable2.getMonto()+costo);
		}
			
		for (CuentaContable cuentaContableTemp : cuentasContables) {

			List<CuentaContable> cuentasTemp = serviceCuentasContables.
						buscarPorEmpresaIdCuentaControl(empresa, cuentaContableTemp.getId());
				
			double valor = 0;
							
			for (CuentaContable cuentaContableTemp2 : cuentasTemp) {
					valor += cuentaContableTemp2.getMonto();
			}
			
			double valorFinal = cuentaContableTemp.getMonto() + valor;

			cuentaContableTemp.setMonto(valorFinal);		
		}
	
		List<CuentaContable> newOrder = new LinkedList<>();
		
		for (int i = cuentasContables.size(); i > 0; i--) {
			CuentaContable cuentaTemp = cuentasContables.get(i-1);
			cuentaTemp.setMonto(formato2d(cuentaTemp.getMonto()));
			newOrder.add(cuentaTemp);
		}

		List<CuentaContable> cuentasContablesAuxiliaresGeneral = serviceCuentasContables.buscarPorEmpresaTipo(empresa, "A");
		List<CuentaContable> cuentasContablesAuxiliaresIniciadas = serviceCuentasContables.buscarPorEmpresaTipoEstado(empresa, "A", 1);
		model.addAttribute("carpeta", carpeta);
		model.addAttribute("empresa", empresa);
		model.addAttribute("cuentasContablesAuxiliaresGeneral", cuentasContablesAuxiliaresGeneral);
		
		for (CuentaContable cuentaContableIni : cuentasContablesAuxiliaresIniciadas) {
			//Verificamos el balance total de la cuenta contable
			List<EntradaDiario> entradasDiarios = serviceEntradasDiarios.buscarPorCuentaContableEmpresaCarpeta(cuentaContableIni, empresa, carpeta);
			double montoTotal = 0.0;
			CuentaContable cuentaContableTemp = cuentaContableIni;
			for (EntradaDiario entradaDiario : entradasDiarios) {
				
				BigDecimal montoT = entradaDiario.getCredito().subtract(entradaDiario.getDebito());
				cuentaContableTemp.setMonto(montoT.doubleValue());
				
				if(cuentaContableTemp.getTipo().equals("C")) {
					List<CuentaContable> cuentasContablesTemp = serviceCuentasContables.
								buscarPorEmpresaCuentaControl(empresa, 
										cuentaContableTemp.getCodigo());
					
					if(!cuentasContablesTemp.isEmpty()) {
						Double montoTemp = 0.0;
						for (CuentaContable cuentaContableTemp2 : cuentasContablesTemp) {
							List<EntradaDiario> entradasDiariosTemp = serviceEntradasDiarios.buscarPorCuentaContableEmpresaCarpeta(cuentaContableTemp2, empresa, carpeta);
							for (EntradaDiario entradaDiarioTemp2 : entradasDiariosTemp) {
								montoTemp+=entradaDiarioTemp2.getCredito().subtract(entradaDiarioTemp2.getDebito()).doubleValue();
							}
							cuentaContableTemp.setMonto(montoTemp);
						}
					}
				}
				
				montoTotal+=cuentaContableTemp.getMonto();
				
			}
			
			cuentaContableIni.setNombreCuenta(cuentaContableIni.getNombreCuenta());
		}
		
		orderCuentaContableTemp = newOrder;
		model.addAttribute("cuentasContables", newOrder);
		model.addAttribute("cuentaContable", new CuentaContable());
		Producto producto = new Producto();
		producto.setEmpresa(empresa);
		return "contabilidad/cuentasContables :: contabilidad";
	}
	
	@PostMapping("/buscarCuentaContable")
	public String buscarCuentaContable(Model model, HttpSession session, String codigo, String nombreCuenta) {
		
		String codigoTemp = codigo.toUpperCase();
		String nombreCuentaTemp = nombreCuenta.toUpperCase();
		
		List<CuentaContable> newOrderDefinitive = new LinkedList<>();
		//Busqueda por codigo
		if(nombreCuenta.equals("") && !codigo.equals("")) {
			newOrderDefinitive = orderCuentaContableTemp.stream().filter(c -> c.getCodigo().toUpperCase().contains(codigoTemp)).collect(Collectors.toList());
		}else if(nombreCuenta.equals("") && codigo.equals("")) {
			newOrderDefinitive = orderCuentaContableTemp;
		}else if(!nombreCuenta.equals("") && codigo.equals("")) {
			newOrderDefinitive = orderCuentaContableTemp.stream().filter(c -> c.getNombreCuenta().toUpperCase().contains(nombreCuentaTemp)).collect(Collectors.toList());
		}
		
		model.addAttribute("cuentasContables", newOrderDefinitive);
		return "contabilidad/cuentasContables :: #tablaCuentasContablesGenerales";
	}
	
	@PostMapping("/crear")
	public ResponseEntity<String> crear(Model model, HttpSession session, 
			String tipoCuenta, String grupoCuenta, String codigoCuenta, String nombreCuenta,
			String cuentaControl, Integer idCuentaControl){
		String response = "0";
		
		if(codigoCuenta.length()>1) {		
			
			if(idCuentaControl == 0) {
				return new ResponseEntity<String>("-3", HttpStatus.ACCEPTED);
			}
			
			String temporalCode = codigoCuenta.substring(0,1);
			//Verificamos que exista el codigo princial
			List<CuentaContable> temporalCuentaContable = serviceCuentasContables.buscarPorEmpresaCodigo((Empresa) session.getAttribute("empresa"), temporalCode);
			if(temporalCuentaContable.isEmpty()) {
				response = "-1";
				return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
			}
		}
		
		CuentaContable cuentaContable = new CuentaContable();
		cuentaContable.setCodigo(codigoCuenta);
		cuentaContable.setTipo(tipoCuenta);
		cuentaContable.setEmpresa((Empresa) session.getAttribute("empresa"));
		cuentaContable.setGrupoCuenta(grupoCuenta);
		cuentaContable.setNombreCuenta(nombreCuenta);
		
		if(cuentaControl.length()>0 && idCuentaControl!=0) {
			CuentaContable cuentaTemp = serviceCuentasContables.buscarPorId(idCuentaControl);
			if(cuentaTemp!=null) {
				cuentaContable.setCuentaControl(cuentaTemp.getCodigo());
				cuentaContable.setIdCuentaControl(idCuentaControl);
				if(cuentaTemp.getTipo().equalsIgnoreCase("A")) {
					//Si la cuenta control es auxiliar no puede guardar
					return new ResponseEntity<String>("-2", HttpStatus.ACCEPTED);
				}
			}
		}
		
		serviceCuentasContables.guardar(cuentaContable);
		
		if(cuentaContable.getId()!=null) {
			response = "1";
		}
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/iniciarContabilidad")
	public ResponseEntity<String> iniciarContabilidad(Model model, HttpSession session,
			Integer idCuentaContable, Integer tipo, Double monto){
		CuentaContable cuentaContable = serviceCuentasContables.buscarPorId(idCuentaContable);
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		String response = "0";

		EntradaIngresoContable entradaIngresoContable = new EntradaIngresoContable();
		entradaIngresoContable.setFecha(new Date());
		entradaIngresoContable.setInfo("inicio contable");
		
		if(tipo == 1) {
			entradaIngresoContable.setTotal(monto);
			entradaIngresoContable.setBalance(monto);
		}else if(tipo == 2){
			entradaIngresoContable.setTotal(monto*-1);
			entradaIngresoContable.setBalance(monto*-1);
		}
		entradaIngresoContable.setEmpresa(empresa);
		entradaIngresoContable.setCuentaContable(cuentaContable);
		entradaIngresoContable.setUsuario(usuario);
		serviceEntradasIngresosContables.guardar(entradaIngresoContable);
		
		if(entradaIngresoContable.getId()!=null) {
			response = "1";
			cuentaContable.setEstado(1);
			serviceCuentasContables.guardar(cuentaContable);
		}
		
		// Buscamos las entradas ingresos contables null ASCENDENTE
		List<EntradaIngresoContable> entradasIngresosContablesNullTemp = serviceEntradasIngresosContables
				.buscarPorEmpresaBalanceContableNullASC(empresa);

		if (!entradasIngresosContablesNullTemp.isEmpty()) {
			for (EntradaIngresoContable entradaIngresoContableNull : entradasIngresosContablesNullTemp) {
				double balanceContableTemp = 0;
				// Buscamos las entradas ingresos contables anteriores con la cuenta contable de
				// la iteracion
				List<EntradaIngresoContable> entradasIngresosContablesXCCNotNUll = serviceEntradasIngresosContables
						.buscarPorEmpresaCuentaContableBalanceContableNotNullMenorQueID(empresa,
								entradaIngresoContableNull.getCuentaContable(), entradaIngresoContableNull.getId());
				if (!entradasIngresosContablesXCCNotNUll.isEmpty()) {
					for (EntradaIngresoContable entradaIngresoContableNotNull : entradasIngresosContablesXCCNotNUll) {
						balanceContableTemp = entradaIngresoContableNotNull.getBalanceContable() == null ? 0
								: entradaIngresoContableNotNull.getBalanceContable();
						break;
					}
					entradaIngresoContableNull.setBalanceContableInicial(balanceContableTemp);
					entradaIngresoContableNull.setBalanceContable(entradaIngresoContableNull.getBalanceContableInicial()
							+ entradaIngresoContableNull.getBalance());
					serviceEntradasIngresosContables.guardar(entradaIngresoContableNull);
				} else {
					entradaIngresoContableNull.setBalanceContableInicial(balanceContableTemp);
					entradaIngresoContableNull.setBalanceContable(entradaIngresoContableNull.getBalanceContableInicial()
							+ entradaIngresoContableNull.getBalance());
					serviceEntradasIngresosContables.guardar(entradaIngresoContableNull);
				}
			}
		}
		
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/cuentasContablesAuxiliar")
	public String cuentasContablesAuxiliar(Model model, HttpSession session){
		List<CuentaContable> cuentasContablesAuxiliares = serviceCuentasContables.buscarPorEmpresaTipoEstado((Empresa) session.getAttribute("empresa"), "A", 0);
		model.addAttribute("cuentasContablesAuxiliares", cuentasContablesAuxiliares);
		return "contabilidad/cuentasContables :: #cuentaContableAuxiliar";
	}
	
	@PostMapping("/buscarContablesAuxiliarCapitalPrestamo")
	public String buscarContablesAuxiliarCapitalPrestamo(Model model, HttpSession session, String valor){
		valor = valor.replace(" ", "");
		List<CuentaContable> cuentasContablesAuxiliares = serviceCuentasContables.
				buscarPorEmpresaTipoAndContieneCodigo((Empresa) session.getAttribute("empresa"), "A", valor);
		CuentaEnlace capitalPrestamoEnlace = new CuentaEnlace();
		capitalPrestamoEnlace.setCuentaContable(new CuentaContable());
		model.addAttribute("capitalPrestamoEnlace", capitalPrestamoEnlace);
		model.addAttribute("cuentasContablesAuxiliaresGeneral", cuentasContablesAuxiliares);
		return "contabilidad/contabilidad :: #capitalPrestamoCC";
	}
	
	@PostMapping("/buscarContablesAuxiliarInteresPrestamo")
	public String buscarContablesAuxiliarInteresPrestamo(Model model, HttpSession session, String valor){
		valor = valor.replace(" ", "");
		List<CuentaContable> cuentasContablesAuxiliares = serviceCuentasContables.
				buscarPorEmpresaTipoAndContieneCodigo((Empresa) session.getAttribute("empresa"), "A", valor);
		CuentaEnlace interesPrestamoEnlace = new CuentaEnlace();
		interesPrestamoEnlace.setCuentaContable(new CuentaContable());
		model.addAttribute("interesPrestamoEnlace", interesPrestamoEnlace);
		model.addAttribute("cuentasContablesAuxiliaresGeneral", cuentasContablesAuxiliares);
		return "contabilidad/contabilidad :: #interesPrestamoCC";
	}
	
	@PostMapping("/buscarContablesAuxiliarGastosCierrePrestamo")
	public String buscarContablesAuxiliarGastosCierrePrestamo(Model model, HttpSession session, String valor){
		valor = valor.replace(" ", "");
		List<CuentaContable> cuentasContablesAuxiliares = serviceCuentasContables.
				buscarPorEmpresaTipoAndContieneCodigo((Empresa) session.getAttribute("empresa"), "A", valor);
		CuentaEnlace gastosCierrePrestamoEnlace = new CuentaEnlace();
		gastosCierrePrestamoEnlace.setCuentaContable(new CuentaContable());
		model.addAttribute("gastosCierrePrestamoEnlace", gastosCierrePrestamoEnlace);
		model.addAttribute("cuentasContablesAuxiliaresGeneral", cuentasContablesAuxiliares);
		return "contabilidad/contabilidad :: #gastosCierrePrestamoCC";
	}
	
	@PostMapping("/buscarContablesAuxiliarCobrosAdicionalesPrestamo")
	public String buscarContablesAuxiliarCobrosAdicionalesPrestamo(Model model, HttpSession session, String valor){
		valor = valor.replace(" ", "");
		List<CuentaContable> cuentasContablesAuxiliares = serviceCuentasContables.
				buscarPorEmpresaTipoAndContieneCodigo((Empresa) session.getAttribute("empresa"), "A", valor);
		CuentaEnlace cobrosAdicionalesPrestamoEnlace = new CuentaEnlace();
		cobrosAdicionalesPrestamoEnlace.setCuentaContable(new CuentaContable());
		model.addAttribute("cobrosAdicionalesPrestamoEnlace", cobrosAdicionalesPrestamoEnlace);
		model.addAttribute("cuentasContablesAuxiliaresGeneral", cuentasContablesAuxiliares);
		return "contabilidad/contabilidad :: #cobrosAdicionalesPrestamoCC";
	}
	
	@PostMapping("/buscarContablesAuxiliarEntradaDiario")
	public String buscarContablesAuxiliarEntradaDiario(Model model, HttpSession session, String valor){
		valor = valor.replace(" ", "");
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Carpeta carpeta = null;
		
		if(session.getAttribute("carpeta") != null) {
			 carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, empresa);
			if(!carpetas.isEmpty()) {
				carpeta = carpetas.get(0);
			}
		}
		List<CuentaContable> cuentasContablesAuxiliares = serviceCuentasContables.
				buscarPorEmpresaTipoEstadoAndContieneCodigo(empresa, "A", 1, valor);
		
		for (CuentaContable cuentaContableIni : cuentasContablesAuxiliares) {
			//Verificamos el balance total de la cuenta contable
			List<EntradaDiario> entradasDiarios = serviceEntradasDiarios.buscarPorCuentaContableEmpresaCarpeta(cuentaContableIni, empresa, carpeta);
			double montoTotal = 0.0;
			CuentaContable cuentaContableTemp = cuentaContableIni;
			for (EntradaDiario entradaDiario : entradasDiarios) {
				
				BigDecimal montoT = entradaDiario.getCredito().subtract(entradaDiario.getDebito());
				cuentaContableTemp.setMonto(montoT.doubleValue());
				
				if(cuentaContableTemp.getTipo().equals("C")) {
					List<CuentaContable> cuentasContablesTemp = serviceCuentasContables.
								buscarPorEmpresaCuentaControl(empresa, 
										cuentaContableTemp.getCodigo());
					
					if(!cuentasContablesTemp.isEmpty()) {
						Double montoTemp = 0.0;
						for (CuentaContable cuentaContableTemp2 : cuentasContablesTemp) {
							List<EntradaDiario> entradasDiariosTemp = serviceEntradasDiarios.buscarPorCuentaContableEmpresaCarpeta(cuentaContableTemp2, empresa, carpeta);
							for (EntradaDiario entradaDiarioTemp2 : entradasDiariosTemp) {
								montoTemp+=entradaDiarioTemp2.getCredito().subtract(entradaDiarioTemp2.getDebito()).doubleValue();
							}
							cuentaContableTemp.setMonto(montoTemp);
						}
					}
				}
				
				montoTotal+=cuentaContableTemp.getMonto();
				
			}
			
			cuentaContableIni.setNombreCuenta(cuentaContableIni.getNombreCuenta()+" -- $ "+montoTotal);
		}
		
		model.addAttribute("cuentasContablesAuxiliaresIniciadas", cuentasContablesAuxiliares);
		return "contabilidad/contabilidad :: #cuentaContableAuxiliarEd";
	}
	
	@PostMapping("/buscarContablesAuxiliar1")
	public String buscarContablesAuxiliar1(Model model, HttpSession session, String valor){
		valor = valor.replace(" ", "");
		List<CuentaContable> cuentasContablesAuxiliares = serviceCuentasContables.
				buscarPorEmpresaTipoEstadoAndContieneCodigo((Empresa) session.getAttribute("empresa"), "A", 1, valor);
		model.addAttribute("cuentasContablesAuxiliaresIniciadas", cuentasContablesAuxiliares);
		return "contabilidad/contabilidad :: #cuentaContableAuxiliarEd1";
	}
	
	@PostMapping("/buscarContablesAuxiliar2")
	public String buscarContablesAuxiliar2(Model model, HttpSession session, String valor){
		valor = valor.replace(" ", "");
		List<CuentaContable> cuentasContablesAuxiliares = serviceCuentasContables.
				buscarPorEmpresaTipoEstadoAndContieneCodigo((Empresa) session.getAttribute("empresa"), "A", 1, valor);
		model.addAttribute("cuentasContablesAuxiliares", cuentasContablesAuxiliares);
		return "contabilidad/contabilidad :: #cuentaContableAuxiliarEd2";
	}
	
	@PostMapping("/guardarEntradaDiario")
	public ResponseEntity<String> guardarEntradaDiario(Model model, HttpSession session, BigDecimal monto,
			String motivo, Integer cuenta1, Integer cuenta2){
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		String response = "0";

		Carpeta carpeta = null;
				
		if(session.getAttribute("carpeta") != null) {
			 carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, empresa);
			if(!carpetas.isEmpty()) {
				carpeta = carpetas.get(0);
			}
		}
		
		CuentaContable cuentaContable1 = serviceCuentasContables.buscarPorId(cuenta1);
		CuentaContable cuentaContable2 = serviceCuentasContables.buscarPorId(cuenta2);
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		
		//Debito
		EntradaDiario entradaDebito = new EntradaDiario();
		entradaDebito.setCarpeta(carpeta);
		entradaDebito.setEmpresa(empresa);
		entradaDebito.setCredito(new BigDecimal(0.0));
		entradaDebito.setCuentaContable(cuentaContable1);
		entradaDebito.setDebito(monto);
		entradaDebito.setDetalle(motivo);
		entradaDebito.setFecha(new Date());
		entradaDebito.setUsuario(usuario);
		
		serviceEntradasDiarios.guardar(entradaDebito);
		
		//Credito
		EntradaDiario entradaCredito = new EntradaDiario();
		entradaCredito.setCarpeta(carpeta);
		entradaCredito.setEmpresa(empresa);
		entradaCredito.setCredito(monto);
		entradaCredito.setCuentaContable(cuentaContable2);
		entradaCredito.setDebito(new BigDecimal(0.0));
		entradaCredito.setDetalle(motivo);
		entradaCredito.setFecha(new Date());
		entradaCredito.setUsuario(usuario);
		
		serviceEntradasDiarios.guardar(entradaCredito);
		
		entradaDebito.setCuentaContableRef(entradaCredito.getCuentaContable());
		entradaCredito.setCuentaContableRef(entradaDebito.getCuentaContable());

		serviceEntradasDiarios.guardar(entradaDebito);
		serviceEntradasDiarios.guardar(entradaCredito);
		
		if(entradaCredito.getId()!=null && entradaDebito.getId()!=null) {
			response = "1";
		}
		
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/guardarCuentaEnlace")
	public ResponseEntity<String> guardarCuentaEnlace(Model model, HttpSession session,
			Integer cobrosAdicionalesPrestamo, Integer gastosCierrePrestamo,
			Integer capitalPrestamo, Integer interesPrestamo){
		String response = "1";		
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		
		//Capital
		
		CuentaEnlace capitalEnlace = serviceCuentasEnlaces.buscarPorEmpresaTipoSeccionReferencia(empresa, "credito", "capital", "prestamos");
		CuentaContable capitalCC = serviceCuentasContables.buscarPorId(capitalPrestamo);
		
		if(capitalEnlace == null) {
			capitalEnlace = new CuentaEnlace();
			capitalEnlace.setEmpresa(empresa);
			capitalEnlace.setCuentaContable(capitalCC);
			capitalEnlace.setReferencia("prestamos");
			capitalEnlace.setSeccion("capital");
			capitalEnlace.setTipo("credito");
			serviceCuentasEnlaces.guardar(capitalEnlace);
		}else {
			capitalEnlace.setCuentaContable(capitalCC);
			serviceCuentasEnlaces.guardar(capitalEnlace);
		}
		
		//Interes
		
		CuentaEnlace interesEnlace = serviceCuentasEnlaces.buscarPorEmpresaTipoSeccionReferencia(empresa, "credito", "interes", "prestamos");
		CuentaContable interesCC = serviceCuentasContables.buscarPorId(interesPrestamo);
		
		if(interesEnlace == null) {
			interesEnlace = new CuentaEnlace();
			interesEnlace.setEmpresa(empresa);
			interesEnlace.setCuentaContable(interesCC);
			interesEnlace.setReferencia("prestamos");
			interesEnlace.setSeccion("interes");
			interesEnlace.setTipo("credito");
			serviceCuentasEnlaces.guardar(interesEnlace);
		}else {
			interesEnlace.setCuentaContable(interesCC);
			serviceCuentasEnlaces.guardar(interesEnlace);
		}
		
		//Gastos Cierre
		
		CuentaEnlace gastosCierreEnlace = serviceCuentasEnlaces.buscarPorEmpresaTipoSeccionReferencia(empresa, "credito", "gastosCierre", "prestamos");
		CuentaContable gastosCierreCC = serviceCuentasContables.buscarPorId(gastosCierrePrestamo);
		
		if(gastosCierreEnlace == null) {
			gastosCierreEnlace = new CuentaEnlace();
			gastosCierreEnlace.setEmpresa(empresa);
			gastosCierreEnlace.setCuentaContable(gastosCierreCC);
			gastosCierreEnlace.setReferencia("prestamos");
			gastosCierreEnlace.setSeccion("gastosCierre");
			gastosCierreEnlace.setTipo("credito");
			serviceCuentasEnlaces.guardar(gastosCierreEnlace);
		}else {
			gastosCierreEnlace.setCuentaContable(gastosCierreCC);
			serviceCuentasEnlaces.guardar(gastosCierreEnlace);
		}
		
		//Cobros Adicionales
		
		CuentaEnlace cuentaEnlaceAdicionales = serviceCuentasEnlaces.buscarPorEmpresaTipoSeccionReferencia(empresa, "credito", "adicionales", "prestamos");
		CuentaContable cuentaContableAdicional = serviceCuentasContables.buscarPorId(cobrosAdicionalesPrestamo);
		
		if(cuentaEnlaceAdicionales == null) {
			cuentaEnlaceAdicionales = new CuentaEnlace();
			cuentaEnlaceAdicionales.setEmpresa(empresa);
			cuentaEnlaceAdicionales.setCuentaContable(cuentaContableAdicional);
			cuentaEnlaceAdicionales.setReferencia("prestamos");
			cuentaEnlaceAdicionales.setSeccion("adicionales");
			cuentaEnlaceAdicionales.setTipo("credito");
			serviceCuentasEnlaces.guardar(cuentaEnlaceAdicionales);
		}else {
			cuentaEnlaceAdicionales.setCuentaContable(cuentaContableAdicional);
			serviceCuentasEnlaces.guardar(cuentaEnlaceAdicionales);
		}
		
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/modificar")
	public ResponseEntity<String> modificar(Model model, HttpSession session, 
			String tipoCuenta, String grupoCuenta, String codigoCuenta, String nombreCuenta,
			String cuentaControl, Integer idCuentaControl, Integer id){
		
		String response = "0";
		
		if(idCuentaControl == 0) {
			return new ResponseEntity<String>("-3", HttpStatus.ACCEPTED);
		}
		
		CuentaContable cuentaContable = serviceCuentasContables.buscarPorId(id);
				
		if(codigoCuenta.length()>1) {		
			String temporalCode = codigoCuenta.substring(0,1);
			//Verificamos que exista el codigo princial
			List<CuentaContable> temporalCuentaContable = serviceCuentasContables.buscarPorEmpresaCodigo((Empresa) session.getAttribute("empresa"), temporalCode);
			if(temporalCuentaContable.isEmpty()) {
				response = "-1";
				return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
			}
		}
		
		cuentaContable.setCodigo(codigoCuenta);
		cuentaContable.setTipo(tipoCuenta);
		cuentaContable.setEmpresa((Empresa) session.getAttribute("empresa"));
		cuentaContable.setGrupoCuenta(grupoCuenta);
		cuentaContable.setNombreCuenta(nombreCuenta);
		
		if(cuentaControl.length()>0 && idCuentaControl!=0) {
			CuentaContable cuentaTemp = serviceCuentasContables.buscarPorId(idCuentaControl);
			if(cuentaTemp!=null) {
				cuentaContable.setCuentaControl(cuentaTemp.getCodigo());
				cuentaContable.setIdCuentaControl(idCuentaControl);
				if(cuentaTemp.getTipo().equalsIgnoreCase("A")) {
					//Si la cuenta control es auxiliar no puede guardar
					return new ResponseEntity<String>("-2", HttpStatus.ACCEPTED);
				}
			}
		}
		
		serviceCuentasContables.guardar(cuentaContable);
		
		if(cuentaContable.getId()!=null) {
			response = "1";
		}
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/modificarCuentaContable/{id}")
	public String modificarCuentaContable(Model model, @PathVariable("id") Integer id) {
		CuentaContable cuentaContable = serviceCuentasContables.buscarPorId(id);
		model.addAttribute("cuentaContable", cuentaContable);
		return "contabilidad/cuentasContables :: #editCuentaContable";
	}
	
	@GetMapping("/mostrarCuentasContables")
	public String mostrarCuentasContables(Model model, HttpSession session) {
		List<CuentaContable> cuentasContables = serviceCuentasContables.buscarPorEmpresaOrderByCodigoAsc((Empresa) session.getAttribute("empresa"));
		for (CuentaContable cuentaContable : cuentasContables) {
			if(cuentaContable.getCuentaControl()!=null && cuentaContable.getTipo().equals("C")) {
				cuentaContable.setClase("ccpadre");
			}else if(cuentaContable.getCuentaControl()!=null && cuentaContable.getTipo().equals("C")) {
				
			}
		}
		model.addAttribute("cuentasContables", cuentasContables);
		return "contabilidad/cuentasContables :: #tablaCuentasContables";
	}
	
	@GetMapping("/imprimirCuentasContables")
	public String imprimirCuentasContables(Model model, HttpSession session) {
		List<CuentaContable> cuentasContables = serviceCuentasContables.buscarPorEmpresaOrderByCodigoAsc((Empresa) session.getAttribute("empresa"));
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		model.addAttribute("cuentasContables", cuentasContables);
		model.addAttribute("empresa", empresa);
		return "impresiones/contabilidad/contabilidad :: #testCarga";
	}
	
	@PostMapping("/eliminar")
	public ResponseEntity<String> eliminar(Integer idCuentaContable){
		CuentaContable cuentaContable = serviceCuentasContables.buscarPorId(idCuentaContable);
		serviceCuentasContables.eliminar(cuentaContable);
		return new ResponseEntity<String>("1", HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/buscarCuentaControl/{codigoCuenta}")
	public ResponseEntity<String[]> buscarCuentaControl(Model model, HttpSession session,
			@PathVariable("codigoCuenta") String codigoCuenta) {
		String response[] = {"0","0"};
		String tempCode = "";
		if(codigoCuenta.contains("-")) {
			//Obtenemos la posicion del ultimo -
			 int position = codigoCuenta.lastIndexOf("-");
			 tempCode = codigoCuenta.substring(0, position);
		}else {
			int longitud = codigoCuenta.length()-1;
			tempCode = codigoCuenta.substring(0, longitud);
		}
		
		List<CuentaContable> cuentaContables = serviceCuentasContables.buscarPorEmpresaCodigo((Empresa) session.getAttribute("empresa"), tempCode);
		if(!cuentaContables.isEmpty()) {
			response[0] = cuentaContables.get(0).getNombreCuenta();
			response[1] = cuentaContables.get(0).getId().toString();
		}
	
		return new ResponseEntity<String[]>(response, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/cuentaDelProducto/{id}")
	public ResponseEntity<String> cuentaDelProducto(Model model, HttpSession session, @PathVariable("id") Integer id) {
		Producto producto = serviceProductos.buscarPorId(id);
		String response = "";
		if(producto!=null) {
			response = producto.getCuentaContable().getCodigo()+" "+producto.getCuentaContable().getNombreCuenta();
		}
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}

}
