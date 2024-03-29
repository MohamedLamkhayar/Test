package org.sid.web;

import java.util.Optional;

import org.sid.entities.Compte;
import org.sid.entities.Operations;
import org.sid.metier.IBanqueMetier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BanqueController {
	@Autowired
	private IBanqueMetier banqueMetier;
	@RequestMapping("/operations")
	public String index() {
		return "comptes";
	}
	
	@RequestMapping("/consulterCompte")
	public String consulter(Model model,String codeCompte,
			@RequestParam(name="page",defaultValue="0")int page,
			@RequestParam(name="size",defaultValue="5")int size) {
		model.addAttribute("codeCompte", codeCompte);
		try {
			Optional<Compte> Ocp=banqueMetier.consulterCompte(codeCompte);
			Compte cp=Ocp.get();
			Page<Operations> pageOprations=
					banqueMetier.listOperation(codeCompte, page, size);
					model.addAttribute("listeOperations", pageOprations.getContent());
					int[] pages=new int[pageOprations.getTotalPages()];
					model.addAttribute("pages", pages);
					model.addAttribute("compte", cp);
		} catch (Exception e) {
			model.addAttribute("exception", e);
		}
		return "comptes";
	}
	@RequestMapping(value="/saveOperation",method=RequestMethod.POST)
	public String saveOperation(Model model,String typeOperation,String codeCompte,double montant,String codeCompte2) {
		try {
			if (typeOperation.equals("VERS")) {
				banqueMetier.verser(codeCompte, montant);
			}
			else if (typeOperation.equals("RETR")) {
				banqueMetier.retirer(codeCompte, montant);
			}
			if (typeOperation.equals("VIR")) {
				banqueMetier.virement(codeCompte, codeCompte2, montant);
			}
		} catch (Exception e) {
			model.addAttribute("error", e);
			return "redirect:/consulterCompte?codeCompte="+codeCompte+"$error="+e.getMessage();
		}
		
		return "redirect:/consulterCompte?codeCompte="+codeCompte;
		
	}
	
}
