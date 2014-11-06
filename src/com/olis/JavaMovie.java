package com.olis;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.olis.EditDialog.onCloseListener;

public class JavaMovie extends JFrame
{

	private static final long        serialVersionUID = 1L;

	private JPanel                   contentPane;
	private JMenuBar                 menuBar;
	private JTable                   table;

	private final String[]           TableHeader      = new String[] { "日期", "預告", "片名 (點兩下播放)", "爸", "媽", "姊", "弟", "妹" };
	private static LinkedList<Movie> mMovieList       = new LinkedList<Movie>();
	
	public static int                OS               = 0;
	public static final int          OS_Windows       = 0;
	public static final int          OS_Mac           = 1;
	
	private boolean                  isGODMode        = false;
	private boolean                  GODMode1         = false;
	private boolean                  GODMode2         = false;
	private boolean                  GODMode3         = false;
	private boolean                  GODMode4         = false;
	
	public static void main(String[] args)
	{
		Movie.createTable();
		initFrame();
	}
	
	private static void initFrame()
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					JavaMovie frame = new JavaMovie();
					frame.setTitle("JavaMovie");
					frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public JavaMovie()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 800);
		contentPane = new JPanel();
		setContentPane(contentPane);
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 30, 30, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);
		
		contentPane.setFocusable(true);
		contentPane.addKeyListener(new KeyAdapter()
		{
			@Override
            public void keyPressed(KeyEvent e)
            {
	            super.keyPressed(e);
	            switch (e.getKeyCode())
                {
					case KeyEvent.VK_0 :
						if (!GODMode1)
						{
							GODMode1 = true;
						}
						else if(GODMode1 && GODMode2 && !GODMode3)
						{
							GODMode3 = true;
						}
						else
						{
							initGODMode();
						}
						break;

					case KeyEvent.VK_9 :
						if (GODMode1 && !GODMode2)
						{
							GODMode2 = true;
						}
						else if(GODMode1 && GODMode2 && GODMode3 && !GODMode4)
						{
							GODMode4 = true;
							isGODMode = true;
							menuBar.setVisible(true);
						}
						else
						{
							initGODMode();
						}
						break;
						
					default :
						initGODMode();
				}
            }
		});
		
		detectOS();
		initMenuBar();
		initTable();		
	}
	
	private void initGODMode()
	{
		GODMode1 = false;
		GODMode2 = false;
		GODMode3 = false;
		GODMode4 = false;
	}

	private void detectOS()
	{
		final String OSName = System.getProperty("os.name").toLowerCase();
		
		if (isWindows(OSName))
		{
			OS = OS_Windows;
		}
		else if (isMac(OSName))
		{
			OS = OS_Mac;
		}
		else
		{
			OS = OS_Windows;
		}
	}
	
	private boolean isWindows(String OSName)
	{
		return (OSName.indexOf("win") >= 0);
	}

	private boolean isMac(String OSName)
	{
		return (OSName.indexOf("mac") >= 0);
	}
	
	private void initMenuBar()
    {
	    JPanel panel = new JPanel();
	    panel.setBorder(UIManager.getBorder("MenuItem.border"));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.WEST;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		contentPane.add(panel, gbc_panel);

		Font font = new Font("新細明體", Font.BOLD, 20);
		
		menuBar = new JMenuBar();
		menuBar.setVisible(false);
		menuBar.setBackground(Color.WHITE);
		menuBar.setBorderPainted(false);
		panel.add(menuBar);
		
		final JMenu Menu_Fun = new JMenu("功能");
		Menu_Fun.setFont(font);
		menuBar.add(Menu_Fun);
		
		final JMenuItem Menu_Fun_New = new JMenuItem("新增影片");
		Menu_Fun_New.setFont(font);
		Menu_Fun.add(Menu_Fun_New);
		
		Menu_Fun_New.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				EditDialog editDialog = new EditDialog(new onCloseListener()
				{
					@Override
					public void onConfirm(Movie movie)
					{
						((DefaultTableModel) table.getModel()).addRow(movie.getContent());
						mMovieList.add(movie);
					}

					@Override
                    public void onDelete()
                    {
	                    
                    }
				});
				editDialog.setModal(true);
				editDialog.setVisible(true);
			}
		});
		
		final JMenuItem Menu_Fun_AutoDirectory = new JMenuItem("自動解析");
		Menu_Fun_AutoDirectory.setFont(font);
		Menu_Fun.add(Menu_Fun_AutoDirectory);
		
		final JLabel AutoDirectoryPath = new JLabel(Movie.getAutoDirectoryPath());
		AutoDirectoryPath.setFont(font);
		menuBar.add(AutoDirectoryPath);
		Menu_Fun_AutoDirectory.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File(""));
				chooser.setDialogTitle("選擇資料夾");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				if (chooser.showOpenDialog(JavaMovie.this) == JFileChooser.APPROVE_OPTION)
				{
					String path = chooser.getSelectedFile().getAbsolutePath();
					Movie.updateAutoDirectoryPath(path);
					AutoDirectoryPath.setText(path);
					
					Movie.runAutoDirectory();
				}
			}
		});
		
		Movie.runAutoDirectory();
    }

	private void initTable()
	{
		JScrollPane scrollPane = new JScrollPane();
		
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 10, 10, 10);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		contentPane.add(scrollPane, gbc_scrollPane);

		initTableData();

		Object [][] data = new Object[mMovieList.size()][TableHeader.length];
		for (int i = 0; i < mMovieList.size(); i++)
		{
			Movie movie = mMovieList.get(i);
			data[i] = movie.getContent();
		}
		
		table = new JTable(new DefaultTableModel(data, TableHeader))
		{
            private static final long serialVersionUID = 1L;
			@Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
            {
				Component component = super.prepareRenderer(renderer, row, column);
				
				if(table.isCellSelected(row, column))
				{
					component.setForeground(new Color(0x00, 0x00, 0x00));
					component.setBackground(new Color(0xf5, 0xa6, 0x23));
				}
				else if(row % 2 == 0)
				{
					component.setForeground(new Color(0x00, 0x00, 0x00));
					component.setBackground(new Color(0xe4, 0xe4, 0xe4));
				}
				else
				{
					component.setForeground(new Color(0x00, 0x00, 0x00));
					component.setBackground(new Color(0xba, 0xd2, 0xec));
				}
	            return component;
            }
			@Override
			public Object getValueAt(int row, int column) 
			{
				if(column == 2)
				{
					return "  " + super.getValueAt(row, column);
				}
				return super.getValueAt(row, column);
			}
		};
		table.setCellSelectionEnabled(true);
		table.setEnabled(false);
		table.setFillsViewportHeight(true);
		
		Font font = new Font("新細明體", Font.BOLD, 25);
		table.getTableHeader().setFont(font);
		table.getTableHeader().setPreferredSize(new Dimension(0, 50));
		table.setFont(font);
		
//		DefaultTableCellRenderer hdr = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
//		hdr.setHorizontalAlignment(SwingConstants.CENTER);
		
		// 文字置中
		DefaultTableCellRenderer render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumn(TableHeader[0]).setCellRenderer(render);
		table.getColumn(TableHeader[1]).setCellRenderer(render);
		table.getColumn(TableHeader[3]).setCellRenderer(render);
		table.getColumn(TableHeader[4]).setCellRenderer(render);
		table.getColumn(TableHeader[5]).setCellRenderer(render);
		table.getColumn(TableHeader[6]).setCellRenderer(render);
		table.getColumn(TableHeader[7]).setCellRenderer(render);
		
		
		// 開啟自動排序
		table.setAutoCreateRowSorter(true);
		// 初始 (日期) 排序
		table.getRowSorter().toggleSortOrder(0);
		table.getRowSorter().toggleSortOrder(0);
		
		// 欄位寬度
		TableColumnModel tcm = table.getColumnModel();
		tcm.getColumn(0).setPreferredWidth(200);
		tcm.getColumn(1).setPreferredWidth(120);
		tcm.getColumn(2).setPreferredWidth(300);
		tcm.getColumn(0).setMaxWidth(200);
		tcm.getColumn(1).setMaxWidth(120);
		
		final int booleanWidth = 60;
		tcm.getColumn(3).setPreferredWidth(booleanWidth);
		tcm.getColumn(4).setPreferredWidth(booleanWidth);
		tcm.getColumn(5).setPreferredWidth(booleanWidth);
		tcm.getColumn(6).setPreferredWidth(booleanWidth);
		tcm.getColumn(7).setPreferredWidth(booleanWidth);
		tcm.getColumn(3).setMaxWidth(booleanWidth);
		tcm.getColumn(4).setMaxWidth(booleanWidth);
		tcm.getColumn(5).setMaxWidth(booleanWidth);
		tcm.getColumn(6).setMaxWidth(booleanWidth);
		tcm.getColumn(7).setMaxWidth(booleanWidth);
		
		// Row Height
		table.setRowHeight(45);
		
		scrollPane.setViewportView(table);
		
		// Table onClick
		table.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e)
			{
				int col = table.columnAtPoint(e.getPoint());
				int row = table.rowAtPoint(e.getPoint());
				
				table.setRowSelectionInterval(row, row);
				table.setColumnSelectionInterval(col, col);
				
				row = table.convertRowIndexToModel(row);
				
				// Double Click
				if(e.getClickCount() == 2)
				{					
					if (col >= 0 && row >= 0)
					{
						Movie movie = mMovieList.get(row);
						if(col == 0)
						{
							showEditMovieDialog(movie, row);
						}
						else if(col == 1)
						{
							if(!movie.trailerpath.equals(""))
							{
								openfile(movie.trailerpath);
							}
						}
						else if(col == 2)
						{
							openfile(movie.moviepath);
						}
						else if(col > 2)
						{
							switch (col)
                            {
								case 3 :
									movie.father = !movie.father; break;
								case 4 :
									movie.mother = !movie.mother; break;
								case 5 :
									movie.osister = !movie.osister; break;
								case 6 :
									movie.jave = !movie.jave; break;
								case 7 :
									movie.ysister = !movie.ysister; break;
							}
							Movie.updateMovieLooked(movie);
							String [] data = movie.getContent();
							((DefaultTableModel) table.getModel()).setValueAt(data[col], row, col);
						}
					}
				}
			}
		});
	}
	
	private void showEditMovieDialog(final Movie movie, final int row)
	{
		if(isGODMode)
		{			
			EditDialog editDialog = new EditDialog(movie, new onCloseListener()
			{
				@Override
				public void onConfirm(Movie movie)
				{
					String [] data = movie.getContent();
					for(int col=0; col<data.length; col++)
					{
						((DefaultTableModel) table.getModel()).setValueAt(data[col], row, col);
					}
				}
				
				@Override
				public void onDelete()
				{
					((DefaultTableModel) table.getModel()).removeRow(row);
					mMovieList.remove(movie);
				}
			});
			editDialog.setModal(true);
			editDialog.setVisible(true);
		}
	}

	private void initTableData()
	{
		Connection connect = null;
		Statement statement = null;
		try
		{
			Class.forName("org.sqlite.JDBC");
			connect = DriverManager.getConnection("jdbc:sqlite:JavaMovie.db");

			statement = connect.createStatement();

			mMovieList.clear();
			ResultSet rs = statement.executeQuery("SELECT * FROM MOVIE WHERE ENABLE = 1;");
			while (rs.next())
			{
				Movie movie = new Movie
				(
					rs.getInt("id"), 
					rs.getString("moviedate"), 
					rs.getString("moviename"), 
					rs.getString("moviepath"), 
					rs.getString("trailerpath"), 
					rs.getBoolean("father"), 
					rs.getBoolean("mother"), 
					rs.getBoolean("osister"), 
					rs.getBoolean("java"), 
					rs.getBoolean("ysister")
				);
				mMovieList.add(movie);
			}
			rs.close();
			
			statement.close();
			connect.close();
		}
		catch (Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}
	
	private void openfile(String path)
	{
		try
		{
			String [] cmd = null;
			if(JavaMovie.OS == JavaMovie.OS_Windows)
			{
				cmd = new String [] {"cmd.exe", "/C", path};
			}
		    else if(JavaMovie.OS == JavaMovie.OS_Mac)
			{
		    	cmd = new String [] {"open", path};
			}
			
			if(cmd != null)
			{
				Runtime.getRuntime().exec(cmd);			
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
