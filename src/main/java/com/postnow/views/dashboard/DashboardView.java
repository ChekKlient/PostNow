package com.postnow.views.dashboard;

import com.postnow.backend.model.Post;
import com.postnow.backend.model.User;
import com.postnow.backend.service.PostService;
import com.postnow.backend.service.UserService;
import com.postnow.views.postnow.PostNowView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Route(value = "me/dashboard", layout = PostNowView.class)
@PageTitle("Dashboard")
@CssImport(value = "styles/views/dashboard/dashboard-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class DashboardView extends Div implements AfterNavigationObserver{

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    private Grid<Post> postGrid;
    private Button postButton;
    private TextArea textArea;
    private User user;

    public DashboardView() {
        setId("dashboard-view");
        addClassName("dashboard-view");
        setMaxWidth("1366px");
        setHeightFull();

        createNewPost();
        createGrid();

        postButton.addClickListener(e -> {
            Post post = new Post();
            post.setText(textArea.getValue());
            post.setUser(user);

            postService.createPost(post);
            refreshGrid();
            textArea.clear();

            Notification.show("Saved");
        });
    }

    private void createNewPost() {
        HorizontalLayout newPost = new HorizontalLayout();
        newPost.addClassName("newPost");
        newPost.setSpacing(false);
        newPost.getThemeList().add("spacing-s");

        newPost.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        newPost.setWidthFull();

        textArea = new TextArea();
        textArea.setPlaceholder("Write something...");
        textArea.setAutofocus(true);
        textArea.setPreventInvalidInput(true);
        textArea.setMaxLength(500);

        textArea.setWidth("91%");
        textArea.setMaxHeight("5.15em");

        newPost.add(textArea);
        createPostButton(newPost);

        add(newPost);
    }

    private void createPostButton(HorizontalLayout newPost) {
        postButton = new Button();
        postButton.setText("POST");
        newPost.add(postButton);
    }

    private void createGrid() {
        postGrid = new Grid<>();

        postGrid.setHeight("80%");
        postGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        postGrid.addComponentColumn(this::createCard);
        add(postGrid);
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

        Span name;
        if (userPost.getUser().getId().equals(this.user.getId())) {
            RouterLink routerLink = new <Long, UserView>RouterLink("@You", UserView.class, user.getId());
            name = new Span(routerLink);
        }
        else {
            RouterLink routerLink = new <Long, UserView>RouterLink("@" + userPost.getUser().getUserAdditionalData().getFirstName(), UserView.class, userPost.getUser().getId());
            name = new Span(routerLink);
        }
        name.addClassName("name");

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Span date = new Span(userPost.getDate().format(format));
        date.addClassName("date");
        header.add(name, date);

        Span post = new Span(userPost.getText());
        post.addClassName("post");

        var actions = createActionButtons(userPost);

        description.add(header, post, actions);
        card.add(image, description);

        return card;
    }

    private HorizontalLayout createActionButtons(Post userPost) {
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

        likeIcon.addClickListener(event -> {
            if(Optional.ofNullable(likeIcon.getColor()).isPresent() && likeIcon.getColor().equals("red")){
//    todo fix            postService.decrementLikes(userPost);
                likeIcon.setColor("grey");
                Notification.show("You dont't like it");
            }
            else {
//    todo fix            postService.incrementLikes(userPost);
                likeIcon.setColor("red");
                Notification.show("You like it");
            }
        });

        actions.add(likeIcon, likes, commentIcon, comments, shareIcon, shares);

//        commentIcon.addClickListener(event -> {
//            Notification.show("Not implemented");
//        });
//
//        shareIcon.addClickListener(event -> {
//            Notification.show("Not implemented");
//        });

        return actions;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        setThisUser();
        // Set some data when this view is displayed.
        List<Post> postList = postService.findAllByOrderByDateDesc();
        postGrid.setItems(postList);
    }

    private void refreshGrid() {
        Objects.requireNonNull(postGrid, "Grid cannot be null.");

        ListDataProvider<Post> dp = (ListDataProvider<Post>) postGrid.getDataProvider();
        dp.getItems().removeAll(((ListDataProvider<Post>) postGrid.getDataProvider()).getItems());
        dp.getItems().addAll(postService.findAllByOrderByDateDesc());
        dp.refreshAll();
    }

    private void setThisUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String principalEmail = ((UserDetails) principal).getUsername();
        this.user = userService.findUserByEmail(principalEmail).get();
    }
}
