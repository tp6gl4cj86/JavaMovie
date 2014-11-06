package com.olis;

import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class LoadingDialog extends JDialog
{
	
    private static final long serialVersionUID = 1L;

	public LoadingDialog()
	{
		getContentPane().setLayout(null);

		Font font = new Font("�s�ө���", Font.BOLD, 25);

		JLabel label = new JLabel("Ū����Ƥ�...", SwingConstants.CENTER);
		if (JavaMovie.OS == JavaMovie.OS_Windows)
		{
			setBounds(100, 100, 420, 240);
			label.setBounds(0, 0, 420, 240);
		}
		else if (JavaMovie.OS == JavaMovie.OS_Mac)
		{
			setBounds(100, 100, 400, 200);
			label.setBounds(0, 0, 400, 178);
		}
		label.setFont(font);
		getContentPane().add(label);
	}
}
