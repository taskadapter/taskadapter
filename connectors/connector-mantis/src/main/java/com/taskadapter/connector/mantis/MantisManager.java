package com.taskadapter.connector.mantis;

import biz.futureware.mantis.rpc.soap.client.AccountData;
import biz.futureware.mantis.rpc.soap.client.FilterData;
import biz.futureware.mantis.rpc.soap.client.IssueData;
import biz.futureware.mantis.rpc.soap.client.IssueHeaderData;
import biz.futureware.mantis.rpc.soap.client.MantisConnectLocator;
import biz.futureware.mantis.rpc.soap.client.MantisConnectPortType;
import biz.futureware.mantis.rpc.soap.client.ProjectData;
import biz.futureware.mantis.rpc.soap.client.RelationshipData;
import com.google.common.base.Strings;

import javax.xml.rpc.ServiceException;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <b>Entry point</b> for the API: use this class to communicate with Mantis SOAP services.
 */
public class MantisManager {
    /* RelationType
    define( 'BUG_DUPLICATE',	0 );
	define( 'BUG_RELATED',		1 );
	define( 'BUG_DEPENDANT',	2 );
	define( 'BUG_BLOCKS', 3 );
	define( 'BUG_HAS_DUPLICATE', 4 );
     */

    //ETA:'10:none,20:< 1 day,30:2-3 days,40:< 1 week,50:< 1 month,60:> 1 month';

    private static final boolean PRINT_DEBUG = false;
    private static final int DEFAULT_ITEMS_PER_PAGE = 25;

    private String host;
    private String login;
    private String password;

    private int itemsPerPage = DEFAULT_ITEMS_PER_PAGE;

    private MantisConnectPortType connector = null;

    /**
     * Creates a new instance or returns existing instance of MantisConnectPortType. Which used to communicate with Mantis SOAP service.
     *
     * @return MantisConnectPortType instance.
     */
    private MantisConnectPortType getConnector() {
        if (connector == null) {
            MantisConnectLocator locator = new MantisConnectLocator();
            locator.setMantisConnectPortEndpointAddress(host
                    + "/api/soap/mantisconnect.php");

            try {
                connector = locator.getMantisConnectPort();
            } catch (ServiceException e) {
                throw new RuntimeException("Can't connect to Mantis: " + e.toString(), e);
            }
        }

        return connector;
    }

    /**
     * Creates an instance of MantisManager.
     *
     * @param uri complete Mantis server web URI including protocol and port number. Example: http://localhost:8008/
     */
    public MantisManager(String uri) {
        if (uri == null || uri.isEmpty()) {
            throw new IllegalArgumentException(
                    "The host parameter is NULL or empty");
        }
        this.host = uri;
    }

    public MantisManager(String uri, String login, String password) {
        this(uri);
        this.login = login;
        this.password = password;
    }

    /**
     * Returns version of Mantis server.
     *
     * @return Mantis server version
     */
    public String getVersion() throws RemoteException {
        return getConnector().mc_version();
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    /**
     * Returns list of projects which are accessible for the current user.
     *
     * @return List of projects.
     */
    public List<ProjectData> getProjects() throws RemoteException {
        return new ArrayList<>(Arrays.asList(getConnector()
                .mc_projects_get_user_accessible(login, password)));
    }

    /**
     * Returns the project by identifier.
     *
     * @param projectId The project identifier.
     * @return Project instance.
     * @throws RemoteException
     */
    public ProjectData getProjectById(BigInteger projectId)	throws RemoteException {
        if (projectId.intValue() > 0) {
            List<ProjectData> projectList = this.getProjects();

            for (ProjectData project : projectList) {
                if (project.getId().equals(projectId)) {
                    return project;
                }
            }
        }
        return null;
    }

    /**
     * Creates a new project.
     *
     * @param project The project to create.
     * @return Project with projectId property.
     * @throws RemoteException
     */
    public BigInteger createProject(ProjectData project)
            throws RemoteException, RequiredItemException {

        if (Strings.isNullOrEmpty(project.getName())) {
            throw new RequiredItemException("Required property Name is empty");
        }

        BigInteger newId = getConnector().mc_project_add(login, password, project);
        project.setId(newId);

        return newId;
    }

    /**
     * Deletes the project.
     *
     * @param projectId The project to delete.
     * @return True if the project deleted successfully.
     */
    public boolean deleteProject(BigInteger projectId) throws RemoteException {
        return getConnector().mc_project_delete(login, password, projectId);
    }

    /**
     * Retrieves issues for the specified project.
     *
     * @return Issues list.
     */
    public List<IssueData> getIssuesByProject(BigInteger projectId)	throws RemoteException {

        List<IssueData> issues = new ArrayList<>();
        if (projectId.intValue() > 0) {
            int pageNumber = 1;

            List<IssueData> lastItems = new ArrayList<>();

            do {
                List<IssueData> foundItems = Arrays.asList(getConnector()
                        .mc_project_get_issues(login, password, projectId,
                                new BigInteger(String.valueOf(pageNumber)),
                                BigInteger.valueOf(itemsPerPage)));

                // if collection is identical to the collection on previous
                // iteration
                if (lastItems.equals(foundItems)) {
                    break;
                }

                lastItems.clear();
                lastItems.addAll(foundItems);
                issues.addAll(foundItems);

                // if found nothing (works only on the first iteration) or
                // collection contains less than itemsPerPage
                if (foundItems.size() == 0 || foundItems.size() != itemsPerPage) {
                    break;
                }

                pageNumber++;
            } while (true);
        }
        return issues;
    }

    /**
     * Retrieves issues for the specified project.
     *
     * @param projectId
     * @return Issues list.
     * @throws RemoteException
     */
    public List<IssueData> getIssuesByFilter(BigInteger projectId, BigInteger filterId) throws RemoteException {

        List<IssueData> issues = new ArrayList<>();
        if (projectId.intValue() > 0) {
            int pageNumber = 1;

            List<IssueData> lastItems = new ArrayList<>();

            do {
                List<IssueData> foundItems = Arrays.asList(getConnector()
                        .mc_filter_get_issues(login, password, projectId,
                                filterId,
                                new BigInteger(String.valueOf(pageNumber)),
                                BigInteger.valueOf(itemsPerPage)));

                // if collection is identical to the collection on previous
                // iteration
                if (lastItems.equals(foundItems)) {
                    break;
                }

                lastItems.clear();
                lastItems.addAll(foundItems);
                issues.addAll(foundItems);

                // if found nothing (works only on the first iteration) or
                // collection contains less than itemsPerPage
                if (foundItems.size() == 0 || foundItems.size() != itemsPerPage) {
                    break;
                }

                pageNumber++;
            } while (true);
        }
        return issues;
    }

    /**
     * Retrieves the issue by identified.
     *
     * @param issueId
     * @return
     * @throws RemoteException
     */
    public IssueData getIssueById(BigInteger issueId) throws RemoteException {
        return getConnector().mc_issue_get(login, password, issueId);
    }

    /**
     * Tries to find an issue by summary.
     *
     * @param summary
     * @return
     * @throws RemoteException
     */
    public IssueData getIssueBySummary(String summary) throws RemoteException {
        IssueData issue = null;
        BigInteger issueId = getConnector().mc_issue_get_id_from_summary(login, password, summary);

        if (issueId.intValue() > 0) {
            issue = getIssueById(issueId);
        }

        return issue;
    }

    /**
     * Creates a new issue.
     *
     * @param issue
     * @return The issue with specified issueId.
     * @throws RemoteException
     */
    public BigInteger createIssue(IssueData issue) throws RemoteException, RequiredItemException {
        if (Strings.isNullOrEmpty(issue.getSummary())) {
            throw new RequiredItemException("Required property Summary is empty");
        }

        if (Strings.isNullOrEmpty(issue.getCategory())) {
            issue.setCategory("General");
        }

        if (Strings.isNullOrEmpty(issue.getDescription())) {
            throw new RequiredItemException("Required property Description is empty");
        }

        BigInteger newId = getConnector().mc_issue_add(login, password, issue);
        issue.setId(newId);

        return newId;
    }

    /**
     * Updates the specified issue.
     *
     * @param issue
     * @return True if the issue updated successfully.
     * @throws RemoteException
     */
    public boolean updateIssue(IssueData issue) throws RemoteException {
        return getConnector().mc_issue_update(login, password, issue.getId(), issue);
    }

    /**
     * Deletes the specified issue.
     *
     * @param issueId
     * @return True if the issue deleted successfully.
     * @throws RemoteException
     */
    public boolean deleteIssue(BigInteger issueId) throws RemoteException {
        return getConnector().mc_issue_delete(login, password, issueId);
    }

    /**
     * Add the specified issue's relationship.
     *
     * @param issueId
     * @param relation
     * @return True if the relationship created successfully.
     * @throws RemoteException
     */
    public BigInteger createRelationship(BigInteger issueId, RelationshipData relation) throws RemoteException {
        BigInteger id = getConnector().mc_issue_relationship_add(login, password, issueId, relation);
        relation.setId(id);

        return id;
    }

    /**
     * Deletes the specified issue's relationship.
     *
     * @param issueId
     * @return True if the relationship deleted successfully.
     * @throws RemoteException
     */
    public boolean deleteRelationship(BigInteger issueId, BigInteger relationshipId)  throws RemoteException {
        return getConnector().mc_issue_relationship_delete(login, password, issueId, relationshipId);
    }

    /**
     * Retrieves the user list.
     * This operation requires "Mantis Administrator" permission.
     *
     * @return
     * @throws RemoteException
     */
    public List<AccountData> getUsers() throws RemoteException {
        return new ArrayList<>(Arrays.asList(getConnector().mc_project_get_users(login, password, new BigInteger("0"), new BigInteger("0"))));
    }

    /**
     * Retrieves the selected user.
     *
     * @param userId
     * @return
     * @throws RemoteException
     */
    public AccountData getUserById(BigInteger userId) throws RemoteException {
        if (userId.intValue() > 0) {
            List<AccountData> userList = this.getUsers();

            for (AccountData user : userList) {
                if (user.getId().equals(userId)) {
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Tries to get user info based on login information in MantisManage class.
     *
     * @return
     * @throws RemoteException
     */
    public AccountData getCurrentUser() throws RemoteException {
        if (login.length() > 0 && login != null) {
            List<AccountData> userList = this.getUsers();

            for (AccountData user : userList) {
                if (user.getName().equals(login)) {
                    return user;
                }
            }
        }
        return null;
    }

    public FilterData[] getFilters(BigInteger projectId) throws RemoteException {
        return getConnector().mc_filter_get(login, password, projectId);
    }
}
