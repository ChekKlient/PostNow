package com.postnow.views.dashboard;

import com.postnow.backend.model.Post;
import com.postnow.backend.service.PostService;
import com.postnow.views.postnow.PostNowView;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Route(value = "me/dashboard", layout = PostNowView.class)
@PageTitle("Dashboard")
@CssImport(value = "styles/views/dashboard/dashboard-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class DashboardView extends Div implements AfterNavigationObserver {

    @Autowired
    private PostService postService;

    Grid<Post> grid = new Grid<>();

    public DashboardView() {
        setId("dashboard-view");
        addClassName("dashboard-view");
        setSizeFull();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(this::createCard);
        add(grid);
    }

    private HorizontalLayout createCard(Post userPost) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        Image image = new Image();
        image.setSrc(userPost.getUser().getUserAdditionalData().getPhotoURL());
        VerticalLayout description = new VerticalLayout();
        description.addClassName("description");
        description.setSpacing(false);
        description.setPadding(false);

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setSpacing(false);
        header.getThemeList().add("spacing-s");

        RouterLink routerLink = new <Long, UserView> RouterLink("@" + userPost.getUser().getUserAdditionalData().getFirstName(), UserView.class, userPost.getUser().getId());
        Span name = new Span(routerLink);
        name.addClassName("name");

        Span date = new Span(String.valueOf(userPost.getDate()));
        date.addClassName("date");
        header.add(name, date);

        Span post = new Span(userPost.getText());
        post.addClassName("post");

        HorizontalLayout actions = new HorizontalLayout();
        actions.addClassName("actions");
        actions.setSpacing(false);
        actions.getThemeList().add("spacing-s");

        IronIcon likeIcon = new IronIcon("vaadin", "heart");
        Span likes = new Span(String.valueOf(userPost.getLikes()));
        likes.addClassName("likes");
        IronIcon commentIcon = new IronIcon("vaadin", "comment");
        Span comments = new Span(String.valueOf(userPost.getComments()));
        comments.addClassName("comments");
        IronIcon shareIcon = new IronIcon("vaadin", "connect");
        Span shares = new Span(String.valueOf(userPost.getShares()));
        shares.addClassName("shares");

        actions.add(likeIcon, likes, commentIcon, comments, shareIcon, shares);

        description.add(header, post, actions);
        card.add(image, description);
        return card;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // Set some data when this view is displayed.
        List<Post> postList = postService.findAll();
        grid.setItems(postList);
    }
}
