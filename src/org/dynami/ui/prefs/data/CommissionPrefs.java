package org.dynami.ui.prefs.data;

@Prefs.Panel(name="Transaction costs", description="Transaction costs settings")
public class CommissionPrefs extends Prefs {
	@Prefs.Parameter(
			name="Commission per contract",
			description="Commission payed for each traded contract",
			config=PrefsConstants.COMMISSION.PER_CONTRACT,
			type=Prefs.Type.Double,
			defaultValue="2.5")
	public Double costPerContact;
}
