package jacamo.rest.window;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.*;

public class Main extends Application {

    @FXML private TextField text_field_ip;
    @FXML private TextField text_field_port;
    @FXML private TextField text_field_agents_name;
    @FXML private TextField text_field_command;
    @FXML private TextField text_field_msgid;
    @FXML private TextField text_field_performative;
    @FXML private TextField text_field_sender;
    @FXML private TextField text_field_receiver;
    @FXML private TextField text_field_content;
    @FXML private TextField text_field_find;
    @FXML private TextArea text_area_console;
    @FXML private TextArea text_area_plans;
    @FXML private Button button_overview;
    @FXML private Button button_show_all_agents;
    @FXML private Button button_create;
    @FXML private Button button_delete;
    @FXML private Button button_info;
    @FXML private Button button_status;
    @FXML private Button button_log;
    @FXML private Button button_bb;
    @FXML private Button button_plans;
    @FXML private Button button_send_mailbox;
    @FXML private Button button_send_plans;
    @FXML private Button button_send_command;
    @FXML private Button button_find;


    static boolean uriSet = false;
    static URI uri;
    Client client = ClientBuilder.newClient();

    public static void main(String[] args) {
        launch(args);
    }


    public String prettyJsonString(String s){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(s);
        return gson.toJson(je);
    }

    public void clearConsole(ActionEvent actionEvent) {
        this.text_area_console.clear();
    }

    public void find(ActionEvent actionEvent) {

        if (this.text_field_find.getText() != null && !this.text_field_find.getText().isEmpty()) {
            int index = this.text_area_console.getText().indexOf(this.text_field_find.getText());
            if (index == -1) {
                this.text_area_console.appendText("Find key Not in the text");
            } else {
                this.text_area_console.selectRange(index, index +  this.text_field_find.getLength());
            }
        } else {
            this.text_area_console.appendText("Missing find key");
        }
    }

    public void setIPandPort(ActionEvent actionEvent) throws java.net.ConnectException {
        Response response;
        try {
            uri = new URI("http://" + this.text_field_ip.getText() +
                    ":" + this.text_field_port.getText()+"/");
        } catch (URISyntaxException e) {
            this.text_area_console.appendText("Invalid URI Syntax\n\n");
        }
        try{
            client.target(uri.toString()).path("/overview/")
                    .request(MediaType.APPLICATION_JSON).get();
            this.text_area_console.appendText("Successful Connection. URI set!\n\n");
            uriSet = true;
            this.button_overview.setDisable(false);
            this.button_show_all_agents.setDisable(false);
            this.button_create.setDisable(false);
            this.button_delete.setDisable(false);
            this.button_info.setDisable(false);
            this.button_status.setDisable(false);
            this.button_log.setDisable(false);
            this.button_bb.setDisable(false);
            this.button_plans.setDisable(false);
            this.button_send_mailbox.setDisable(false);
            this.button_send_plans.setDisable(false);
            this.button_send_command.setDisable(false);
        } catch (Exception e) {
            this.text_area_console.appendText("Not able to connect. " +
                    "Check if the uri is valid or if there is a multi-agent system" +
                    "running in this uri.\n\n");
        }
    }

    public void getOverview(ActionEvent actionEvent) {
        Response response;
        String rStr;
        String pretty_rStr;

        response = client.target(uri.toString()).path("overview/")
                .request(MediaType.APPLICATION_JSON).get();
        rStr = response.readEntity(String.class).toString();
        pretty_rStr = prettyJsonString(rStr);
        this.text_area_console.appendText("Response (overview/):\n "+
                pretty_rStr +"\n\n");
    }

    public void getAllAgents(ActionEvent actionEvent) {
        Response response;
        String rStr;
        String pretty_rStr;

        response = client.target(uri.toString()).path("agents/")
                .request(MediaType.APPLICATION_JSON).get();
        rStr = response.readEntity(String.class).toString();
        pretty_rStr = prettyJsonString(rStr);
        this.text_area_console.appendText("Response (agents/):\n "+
                pretty_rStr +"\n\n");
    }

    public void postAgent(ActionEvent actionEvent) {
        Response response;
        String rStr;

        Map<String,String> map = new HashMap<>();
        map.put("uri", uri.toString()+"agents/"+
                this.text_field_agents_name.getText());
        map.put("type", "JaCaMoAgent");
        map.put("inbox", uri.toString()+"agents/"+
                this.text_field_agents_name.getText()+"/inbox");

        response = client.target(uri.toString()).path("agents/"+
                        this.text_field_agents_name.getText())
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN)
                .post(Entity.json(new Gson().toJson( map )));
        rStr = response.readEntity(String.class).toString();

        if (response.getStatus() == 200 || response.getStatus() == 201) {
            this.text_area_console.appendText("Agent " +
                    this.text_field_agents_name.getText() + " created!\n\n");
        } else {
            this.text_area_console.appendText("Agent creation failed\n\n");
        }
    }

    public void deleteAgent(ActionEvent actionEvent) {
        Response response;
        String rStr;
        String pretty_rStr;

        response = client.target(uri.toString()).path("agents/"+
                        this.text_field_agents_name.getText())
                .request()
                .delete();
        rStr = response.readEntity(String.class).toString();

        if (response.getStatus() == 200 || response.getStatus() == 201) {
            this.text_area_console.appendText("Agent " +
                    this.text_field_agents_name.getText() + " deleted!\n\n");
        } else {
            this.text_area_console.appendText("Agent deletion failed\n\n");
        }
    }

    public void getAgent(ActionEvent actionEvent) {
        Response response;
        String rStr;
        String pretty_rStr;

        response = client.target(uri.toString()).path("agents/"+
                        this.text_field_agents_name.getText()+"/")
                .request(MediaType.APPLICATION_JSON).get();
        rStr = response.readEntity(String.class).toString();
        pretty_rStr = prettyJsonString(rStr);
        this.text_area_console.appendText("Response (agents/"+
                this.text_field_agents_name.getText()+"):\n "+
                pretty_rStr +"\n\n");
    }

    public void getAgentStatus(ActionEvent actionEvent) {
        Response response;
        String rStr;
        String pretty_rStr;

        response = client.target(uri.toString()).path("agents/"+
                        this.text_field_agents_name.getText()+"/status/")
                .request(MediaType.APPLICATION_JSON).get();
        rStr = response.readEntity(String.class).toString();
        pretty_rStr = prettyJsonString(rStr);
        this.text_area_console.appendText("Response (agents/"+
                this.text_field_agents_name.getText()+"/status):\n "+
                pretty_rStr +"\n\n");
    }

    public void getAgentLog(ActionEvent actionEvent) {
        Response response;
        String rStr;

        response = client.target(uri.toString()).path("agents/"+
                        this.text_field_agents_name.getText()+"/log/")
                .request(MediaType.TEXT_PLAIN).get();
        rStr = response.readEntity(String.class).toString();
        this.text_area_console.appendText("Response (agents/"+
                this.text_field_agents_name.getText()+"/log):\n "+
                rStr +"\n\n");
    }

    public void getAgentBB(ActionEvent actionEvent) {
        Response response;
        String rStr;
        String pretty_rStr;

        response = client.target(uri.toString()).path("agents/"+
                        this.text_field_agents_name.getText()+"/bb/")
                .request(MediaType.APPLICATION_JSON).get();
        rStr = response.readEntity(String.class).toString();
        pretty_rStr = prettyJsonString(rStr);
        this.text_area_console.appendText("Response (agents/"+
                this.text_field_agents_name.getText()+"/bb):\n "+
                pretty_rStr +"\n\n");
    }

    public void getAgentPlans(ActionEvent actionEvent) {
        Response response;
        String rStr;
        String pretty_rStr;

        response = client.target(uri.toString()).path("agents/"+
                        this.text_field_agents_name.getText()+"/plans/")
                .request(MediaType.APPLICATION_JSON).get();
        rStr = response.readEntity(String.class).toString();
        pretty_rStr = prettyJsonString(rStr);
        this.text_area_console.appendText("Response (agents/"+
                this.text_field_agents_name.getText()+"/plans):\n "+
                pretty_rStr +"\n\n");
    }

    public void postInbox(ActionEvent actionEvent) {
        Response response;
        String rStr;

        Map<String,String> map = new HashMap<>();
        map.put("msgId", this.text_field_msgid.getText());
        map.put("performative", this.text_field_performative.getText());
        map.put("sender", this.text_field_sender.getText());
        map.put("receiver", this.text_field_receiver.getText());
        map.put("content", this.text_field_content.getText());

        response = client.target(uri.toString()).path("agents/"+
                        this.text_field_agents_name.getText()+"/inbox")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN)
                .post(Entity.json(new Gson().toJson( map )));
        rStr = response.readEntity(String.class).toString();

        if (response.getStatus() == 200 || response.getStatus() == 201) {
            this.text_area_console.appendText("Message posted:" +
                            "\n\tmsgId: "+this.text_field_msgid.getText()+
                            "\n\tperformative: "+this.text_field_performative.getText()+
                            "\n\tsender: "+this.text_field_sender.getText()+
                            "\n\treceiver: "+this.text_field_receiver.getText()+
                            "\n\tcontent: "+this.text_field_content.getText()+"\n\n");
        } else {
            this.text_area_console.appendText("Message posting failed\n\n");
        }
    }

    public void postPlan(ActionEvent actionEvent) {
        Response response;
        String rStr;

        response = client.target(uri.toString()).path("agents/"+
                        this.text_field_agents_name.getText()+"/plans")
                .request()
                .accept(MediaType.TEXT_PLAIN)
                .post(Entity.json(this.text_area_plans.getText()));
        System.out.println(this.text_area_plans.getText());
        rStr = response.readEntity(String.class).toString();

        if (response.getStatus() == 200 || response.getStatus() == 201) {
            this.text_area_console.appendText("Agent " +
                    this.text_field_agents_name.getText() + "'s plans posted:" +
                    "\n\t"+this.text_area_plans.getText()+"\n\n");
        } else {
            this.text_area_console.appendText("Plans posting failed\n\n");
        }
    }

    public void postCommand(ActionEvent actionEvent) {
        Response response;
        String rStr;

        Form form = new Form();
        form.param("c", this.text_field_command.getText());
        Entity<Form> entity = Entity.form(form);

        response = client.target(uri.toString()).path("agents/"+
                        this.text_field_agents_name.getText()+"/command")
                .request()
                .post(entity);
        rStr = response.readEntity(String.class).toString();

        if (response.getStatus() == 200 || response.getStatus() == 201) {
            this.text_area_console.appendText("Agent " +
                    this.text_field_agents_name.getText() + "'s command posted:" +
                    "\n\t"+this.text_field_command.getText()+"\n\n");
        } else {
            this.text_area_console.appendText("Command posting failed\n\n");
        }
    }

    @FXML
    public void initialize() {
        this.text_field_ip.setText("54.196.249.187");
        this.text_field_port.setText("8080");
        this.text_field_agents_name.setText("alice");
        this.text_area_console.setText("Start setting a valid URI\n\n");
        this.text_area_console.setWrapText(true);

        this.button_overview.setDisable(true);
        this.button_show_all_agents.setDisable(true);
        this.button_create.setDisable(true);
        this.button_delete.setDisable(true);
        this.button_info.setDisable(true);
        this.button_status.setDisable(true);
        this.button_log.setDisable(true);
        this.button_bb.setDisable(true);
        this.button_plans.setDisable(true);
        this.button_send_mailbox.setDisable(true);
        this.button_send_plans.setDisable(true);
        this.button_send_command.setDisable(true);
    }

    @Override
    public void start(Stage primaryStage) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("main.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
