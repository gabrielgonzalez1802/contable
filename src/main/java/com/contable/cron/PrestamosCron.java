package com.contable.cron;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.contable.model.PrestamoDetalle;
import com.contable.service.IPrestamosDetallesService;

@Component
public class PrestamosCron {

	@Autowired
	private IPrestamosDetallesService servicePrestamosDetalles;

	@Scheduled(cron = "0 51 07 * * *")
	public void calculoVencimientoCuota() throws ParseException {
		//Buscamos los detalles vencidos de los prestamos 
		List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorEstado(2);
		if(!prestamoDetalles.isEmpty()) {
			for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
				//verificamos si pasaron los dias de gracia
				LocalDateTime fecha = convertToLocalDateTimeViaInstant(prestamoDetalle.getFechaGenerada());
				//Le sumamos los dias de gracia
				LocalDateTime fechaMax = fecha.plusDays(prestamoDetalle.getPrestamo().getDias_gracia());
				LocalDateTime fechaAcct =  LocalDateTime.now();
				if(fechaAcct.isAfter(fechaMax) || fechaAcct.isEqual(fechaMax)) {
					//Cuotas x interes (interes_mora) / 30
//					double vencidos = diasVencidos(fechaMax, fechaAcct);
					//Se le suma 1 dia ya que se cuenta el mismo dia de vencimiento
//					vencidos+=1;
					//Calculamos los dias vencidos despues de los dias de gracia
//					Double mora = prestamoDetalle.getPrestamo().getPagos() * prestamoDetalle.getInteres() * 30 / vencidos;
					Double mora = prestamoDetalle.getInteres_mora() / 30;
					prestamoDetalle.setMora(formato2d(mora));
					servicePrestamosDetalles.guardar(prestamoDetalle);
				}
			}
		}
	}
	
	@Scheduled(cron = "0 50 07 * * *")
	public void diasVencidos() throws ParseException {
		//Buscamos los detalles pendientes de los prestamos 
		List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorEstado(0);
		if(!prestamoDetalles.isEmpty()) {
			for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
				//verificamos si pasaron los dias de gracia
				LocalDateTime fecha = convertToLocalDateTimeViaInstant(prestamoDetalle.getFechaGenerada());
				LocalDateTime fechaAcct =  LocalDateTime.now();
				if(fecha.isBefore(fechaAcct) || fecha.isEqual(fechaAcct)) {
					prestamoDetalle.setEstado(2);
					servicePrestamosDetalles.guardar(prestamoDetalle);
				}
			}
		}
	}
	
	public double diasVencidos(LocalDateTime fecha1, LocalDateTime fecha2) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
         String fecha1Temp = fecha1.getDayOfMonth()+"/"+fecha1.getMonthValue()+"/"+fecha1.getYear();
         String fecha2Temp = fecha2.getDayOfMonth()+"/"+fecha2.getMonthValue()+"/"+fecha2.getYear();
         Date dt1 = sdf.parse(fecha1Temp);
         Date dt2 = sdf.parse(fecha2Temp);
         long diff = dt2.getTime() - dt1.getTime();
         return diff / 1000L / 60L / 60L / 24L;
	}
	
	public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDateTime();
	}
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}
	
}
