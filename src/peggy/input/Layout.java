package peggy.input;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

/**
 * This class provides an incremental visualization of an EPEG.
 * It pops up a JFrame with a depiction of the values of the EPEG,
 * with each term inside them. For any given child value of a term, 
 * you can click on that value to expand it into its list of terms, etc.
 */
public abstract class Layout extends JPanel {
	private static final long serialVersionUID = 543276L;
	private static final Font LABEL_FONT = new Font("Monospaced", Font.BOLD, 8);
	
	protected abstract Set<? extends Value> getValues();

	protected void build() {
		final Set<? extends Value> values = getValues();
		
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		
		for (Value v : values) {
			JPanel panel = new JPanel();
			panel.add(getValueComponent(v, null));
			content.add(panel);
		}
		
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(content));
	}
	
	class Info {
		public final Term term;
		public final int childIndex;
		public final JComponent parentContainer;
		Info(Term _term, int _childIndex, JComponent _parentContainer) {
			this.term = _term;
			this.childIndex = _childIndex;
			this.parentContainer = _parentContainer;
		}
	}
	
	private JComponent getValueComponent(Value v, Info info) {
		final Color color = v.isRoot ? Color.yellow : Color.lightGray;
		
		final JPanel outerPanel = new JPanel(new BorderLayout());
		final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		outerPanel.add(topPanel, BorderLayout.NORTH);
		outerPanel.setBackground(color);
		outerPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
		topPanel.setBackground(color);
		
		final JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		valuePanel.setBackground(color);
		
		if (info == null) {
			JLabel label = new JLabel("V" + v.id, JLabel.LEFT);
			label.setFont(LABEL_FONT);
			label.setBackground(color);
			topPanel.add(label);
		} else {
			JButton button = new JButton();
			button.setFont(LABEL_FONT);
			button.addActionListener(getCloseListener(info));
			Dimension d = new Dimension(10,10);
			button.setSize(d);
			button.setPreferredSize(d);
			button.setMaximumSize(d);
			topPanel.add(button);
			
			JLabel label = new JLabel("V" + v.id, JLabel.LEFT);
			label.setFont(LABEL_FONT);
			label.setBackground(color);
			topPanel.add(label);
		}
		
		for (Term t : v.terms) {
			valuePanel.add(getTermComponent(t));
		}
		
		outerPanel.add(valuePanel, BorderLayout.CENTER);
		
		return outerPanel;
	}
	
	private JButton getExpandingButton(Info info) {
		JButton button = new JButton("V" + info.term.childValues.get(info.childIndex).id);
		button.setFont(LABEL_FONT);
		button.addActionListener(getChildListener(info));
		return button;
	}
	
	private JComponent getTermComponent(Term t) {
		JPanel termPanel = new JPanel(new BorderLayout());
		termPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		termPanel.setBackground(Color.lightGray);

		if (t.childValues.size() > 0) {
			JPanel childPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			for (int i = 0; i < t.childValues.size(); i++) {
				final JPanel buttonPanel = new JPanel();
				buttonPanel.add(getExpandingButton(new Info(t, i, buttonPanel)));
				buttonPanel.setBackground(Color.lightGray);
				childPanel.add(buttonPanel);
			}
			childPanel.setBackground(Color.lightGray);
			termPanel.add(childPanel, BorderLayout.SOUTH);
		}

		JLabel label = new JLabel(t.label, JLabel.CENTER);
		label.setFont(LABEL_FONT);
		label.setBackground(Color.lightGray);
		termPanel.add(label, BorderLayout.CENTER);
		return termPanel;
	}

	private void relayout() {
		this.invalidate();
		this.validate();
	}
	
	private ActionListener getCloseListener(final Info info) {
		final Runnable runner = new Runnable() {
			public void run() {
				info.parentContainer.removeAll();
				info.parentContainer.add(getExpandingButton(info));
				relayout();
			}
		};

		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				javax.swing.SwingUtilities.invokeLater(runner);
			}
		};
	}
	
	private ActionListener getChildListener(final Info info) {
		final Runnable runner = new Runnable() {
			public void run() {
				info.parentContainer.removeAll();
				info.parentContainer.add(getValueComponent(info.term.childValues.get(info.childIndex), info));
				relayout();
			}
		};
		
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				javax.swing.SwingUtilities.invokeLater(runner);
			}
		};
	}
	
	public class Value {
		public final int id;
		public final boolean isRoot;
		public final Set<Term> terms = new HashSet<Term>();
		public Value(int _id, boolean _isRoot) {
			this.id	= _id;
			this.isRoot = _isRoot;
		}
		public Value(int _id) {
			this(_id, false);
		}
		public Term addTerm(String label, Value... vs) {
			Term t = new Term(label, this);
			t.childValues.addAll(Arrays.asList(vs));
			this.terms.add(t);
			return t;
		}
		public Term addTerm(String label, List<? extends Value> vs) {
			Term t = new Term(label, this);
			t.childValues.addAll(vs);
			this.terms.add(t);
			return t;
		}
	}
	public class Term {
		public final String	label;
		public final Value parentValue;
		public final List<Value> childValues = new ArrayList<Value>();
		private Term(String _label, Value _parent) {
			this.label = _label;
			this.parentValue = _parent;
		}
	}
	
	public static void run(Layout mylayout) {
		JFrame frame = new JFrame("EPEG");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new JScrollPane(mylayout), BorderLayout.CENTER);
		frame.setSize(new Dimension(500,500));
		frame.setVisible(true);
	}
}
