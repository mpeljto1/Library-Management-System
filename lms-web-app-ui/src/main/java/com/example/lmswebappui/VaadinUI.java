package com.example.lmswebappui;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringUI(path = "/")
public class VaadinUI extends UI{

	@Override
	protected void init(VaadinRequest request) {
		
		Label title = new Label("Dashboard");
		
		
		HorizontalLayout topbar = new HorizontalLayout();
		VerticalLayout sidebar = new VerticalLayout();
		VerticalLayout mainArea = new VerticalLayout();
		
		Label books = new Label("Books");
		Label users = new Label("Users");
		Label rents = new Label("Rents");
		Label payments = new Label("Payments");
		
		 Button bookIcon = new Button(VaadinIcons.BOOK);
		
		
		CssLayout cssLayout = new CssLayout();
        cssLayout.addComponents(bookIcon, books);
        cssLayout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        
        
        sidebar.addComponents(cssLayout, users, rents, payments);
        sidebar.setSizeFull();
        
        
        Panel today_reports = new Panel();
        Panel last_day_reports = new Panel();
        Panel last_week_reports = new Panel();
        Panel last_month_reports = new Panel();

		
        mainArea.addComponents(topbar, title, today_reports, last_day_reports, last_week_reports, last_month_reports);
        
        setContent(sidebar);
	}

	
}
