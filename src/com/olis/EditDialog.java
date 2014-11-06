package com.olis;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

public class EditDialog extends JDialog
{

	private static final long serialVersionUID = 1L;

	private JTextField        moviedate;
	private JTextField        moviename;
	private JTextField        moviepath;
	private JTextField        trailerpath;

	private JButton           btnFileChooser;
	private JButton           btnTrailerChooser;
	private JButton           btnConfirm;
	private JButton           btnDelete;

	private JToggleButton     autoParser;
	
	private boolean           isNew;
	private Movie             movie;
	
	/**
	 * @wbp.parser.constructor
	 */
	public EditDialog(onCloseListener closeListener)
	{
		this.isNew = true;
		this.closeListener = closeListener;
		
		initDialog();
		initFileChooser();
	}
	
	public EditDialog(Movie movie, onCloseListener closeListener)
	{
		this.isNew = false;
		this.movie = movie;
		this.closeListener = closeListener;
		
		initDialog();
		initData();
		initFileChooser();
	}

	private onCloseListener closeListener;	
	public interface onCloseListener
	{
		void onConfirm(Movie movie);
		void onDelete();
	}
	
	private void initDialog()
    {
	    getContentPane().setLayout(null);

//	    if(JavaMovie.OS == JavaMovie.OS_Windows)
		{
	    	setBounds(100, 100, 520, 340);
		}
//	    else if(JavaMovie.OS == JavaMovie.OS_Mac)
//		{
//	    	setBounds(100, 100, 500, 300);
//		}

		Font font = new Font("新細明體", Font.BOLD, 17);
		
		JLabel label = new JLabel("日期");
		label.setFont(font);
		label.setBounds(20, 20, 70, 20);
		getContentPane().add(label);

		moviedate = new JTextField();
		moviedate.setFont(font);
		moviedate.setBounds(95, 15, 200, 30);
		getContentPane().add(moviedate);
		moviedate.setColumns(10);

		JLabel label_1 = new JLabel("影片名稱");
		label_1.setFont(font);
		label_1.setBounds(20, 52, 75, 20);
		getContentPane().add(label_1);

		moviename = new JTextField();
		moviename.setFont(font);
		moviename.setBounds(95, 47, 200, 30);
		getContentPane().add(moviename);
		moviename.setColumns(10);

		autoParser = new JToggleButton("自動解析");
		autoParser.setFont(font);
		autoParser.setSelected(true);
		autoParser.setBounds(20, 84, 275, 38);
		getContentPane().add(autoParser);
		
		btnFileChooser = new JButton("選擇影片");
		btnFileChooser.setFont(font);
		btnFileChooser.setBounds(320, 20, 150, 45);
		getContentPane().add(btnFileChooser);

		moviepath = new JTextField();
		moviepath.setFont(font);
		moviepath.setEnabled(false);
		moviepath.setEditable(false);
		moviepath.setBounds(20, 134, 460, 30);
		getContentPane().add(moviepath);
		moviepath.setColumns(10);
		
		btnTrailerChooser = new JButton("選擇預告");
		btnTrailerChooser.setFont(font);
		btnTrailerChooser.setBounds(320, 77, 150, 45);
		getContentPane().add(btnTrailerChooser);
		initTrailerChooser();
		
		trailerpath = new JTextField();
		trailerpath.setFont(font);
		trailerpath.setEnabled(false);
		trailerpath.setEditable(false);
		trailerpath.setColumns(10);
		trailerpath.setBounds(20, 176, 460, 30);
		getContentPane().add(trailerpath);
		
		btnConfirm = new JButton("確認");
		btnConfirm.setFont(font);
		btnConfirm.setBounds(280, 218, 200, 47);
		getContentPane().add(btnConfirm);

		btnConfirm.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(!moviedate.getText().toString().equals("") && !moviename.getText().toString().equals("") && !moviepath.getText().toString().equals(""))
				{
					try
                    {
						if(isNew)
						{				
							Movie movie = new Movie
							(
								-1, 
								moviedate.getText().toString(), 
								moviename.getText().toString(),
								moviepath.getText().toString(), 
								trailerpath.getText().toString(), 
								false, false, false, false, false
							);
							movie.id = Movie.addMovie(movie.moviedate, movie.moviename, movie.moviepath, movie.trailerpath);
							
							if(closeListener != null && movie.id >= 0)
							{
								closeListener.onConfirm(movie);
							}
						}
						else if(movie != null)
						{
							movie.moviedate = moviedate.getText().toString();
							movie.moviename = moviename.getText().toString();
							movie.moviepath = moviepath.getText().toString();
							movie.trailerpath = trailerpath.getText().toString();
							
							Movie.updateMovie(movie);
							
							if(closeListener != null)
							{
								closeListener.onConfirm(movie);
							}
						}
						
						dispose();
                    }
                    catch (Exception ex)
                    {
                    	System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            			System.exit(0);
                    }
				}
			}
		});
		
		btnDelete = new JButton(isNew ? "取消" : "刪除");
		btnDelete.setFont(font);
		btnDelete.setBounds(20, 218, 200, 47);
		getContentPane().add(btnDelete);
		
		btnDelete.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(movie != null)
				{
					Movie.deleteMovie(movie.id);
					if(closeListener != null)
					{
						closeListener.onDelete();
					}
				}
				
				dispose();
			}
		});
    }
	
	private void initData()
	{
		if(movie != null)
		{
			moviedate.setText(movie.moviedate);
			moviename.setText(movie.moviename);
			moviepath.setText(movie.moviepath);
			trailerpath.setText(movie.trailerpath);
		}
	}
	
	private void initFileChooser()
	{
		btnFileChooser.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File(""));
				chooser.setDialogTitle("選擇影片或資料夾");
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				
				if (chooser.showOpenDialog(EditDialog.this) == JFileChooser.APPROVE_OPTION)
				{
					moviepath.setText(chooser.getSelectedFile().getAbsolutePath());
					
					if(autoParser.isSelected())
					{						
						File file = new File(moviepath.getText().toString());
						if(file.isDirectory())
						{
							String [] movieInfo = Movie.chooseDirectory(file, false);
							moviedate  .setText(movieInfo[0]);
							moviename  .setText(movieInfo[1]);
							moviepath  .setText(movieInfo[2]);
							trailerpath.setText(movieInfo[3]);
						}
						else
						{
							chooseFile(chooser, file);
						}
					}
				}
				
			}
		});
	}
	
	private void chooseFile(JFileChooser chooser, File file)
	{
		if(file.getParentFile().getName().length() >= 8)
		{			
			moviedate.setText(file.getParentFile().getName().substring(0, 8));
		}
		moviename.setText(file.getName().substring(0, file.getName().indexOf(".")));
	}
	
	private void initTrailerChooser()
	{
		btnTrailerChooser.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File(""));
				chooser.setDialogTitle("選擇影片");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				
				if (chooser.showOpenDialog(EditDialog.this) == JFileChooser.APPROVE_OPTION)
				{
					trailerpath.setText(chooser.getSelectedFile().getAbsolutePath());
				}
				
			}
		});
	}
	
}
