package com.postnow.views.postnow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.postnow.backend.model.Role;
import com.postnow.backend.security.SecurityConfig;
import com.postnow.backend.security.SecurityUtils;
import com.postnow.views.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;import com.vaadin.flow.theme.lumo.Lumo;

import com.postnow.views.dashboard.DashboardView;
import com.postnow.views.settings.SettingsView;
import com.postnow.views.adminusers.AdminusersView;

import static com.vaadin.flow.component.notification.Notification.Position.TOP_CENTER;
import static com.vaadin.flow.component.notification.Notification.Position.TOP_START;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@CssImport(value = "styles/views/postnow/post-now-view.css", themeFor = "vaadin-app-layout")
@PWA(name = "PostNow", shortName = "PNow")
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
public class PostNowView extends AppLayout implements BeforeEnterObserver {

    private final Tabs menu;

    public PostNowView() {
        setId("post-now-view");
        menu = createMenuTabs();
        addToNavbar(menu);
    }

    private static Tabs createMenuTabs() {
        final Tabs tabs = new Tabs();
        tabs.getStyle().set("max-width", "100%");
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabs.add(getAvailableTabs());
        return tabs;
    }

    private static Tab[] getAvailableTabs() {
        final List<Tab> tabs = new ArrayList<>();
        tabs.add(createTab("Dashboard", DashboardView.class));
        tabs.add(createTab("Settings", SettingsView.class));
        tabs.add(createTab("Admin dashboard", AdminusersView.class));
        tabs.add(createTab(new Anchor("/logout", "Log out")));
        return tabs.toArray(new Tab[tabs.size()]);
    }

    private static Tab createTab(String title, Class<? extends Component> viewClass) {
        return createTab(populateLink(new RouterLink(null, viewClass), title));
    }

    private static Tab createTab(Component content) {
        final Tab tab = new Tab();
        tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        tab.add(content);
        return tab;
    }

    private static <T extends HasComponents> T populateLink(T a, String title) {
        a.add(title);
        return a;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (AdminusersView.class.equals(event.getNavigationTarget())
                && !SecurityUtils.hasRole(Role.ADMIN.name())) {
            if(SecurityUtils.isUserLoggedIn())
                event.rerouteTo(DashboardView.class);
            else
                event.rerouteTo(MainView.class);

            Notification errorNotification = new Notification();
            errorNotification.setText("You don't have access, switch to the admin account");
            errorNotification.setDuration(2000);
            errorNotification.setPosition(TOP_START);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            errorNotification.open();
        }
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        selectTab();
    }

    private void selectTab() {
        String target = RouteConfiguration.forSessionScope().getUrl(getContent().getClass());
        Optional<Component> tabToSelect = menu.getChildren().filter(tab -> {
            Component child = tab.getChildren().findFirst().get();
            return child instanceof RouterLink && ((RouterLink) child).getHref().equals(target);
        }).findFirst();
        tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));
    }
}
