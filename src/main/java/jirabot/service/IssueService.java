package jirabot.service;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.query.QueryImpl;
import com.atlassian.query.clause.TerminalClauseImpl;
import com.atlassian.query.operand.SingleValueOperand;
import com.atlassian.query.operator.Operator;
import com.atlassian.query.clause.Clause;
import com.atlassian.query.Query;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.web.bean.PagerFilter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IssueService {

    public String getOpenIssues() {
        try {
            ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
            SearchService searchService = ComponentAccessor.getComponent(SearchService.class);

            Clause clause = new TerminalClauseImpl("status", Operator.EQUALS, new SingleValueOperand("Open"));
            Query query = new QueryImpl(clause, null, null);

            List<Issue> issues = searchService.search(user, query, PagerFilter.getUnlimitedFilter())
                    .getIssues();

            if (issues.isEmpty()) return "Нет открытых задач.";

            return issues.stream()
                    .map(issue -> issue.getKey() + ": " + issue.getSummary())
                    .collect(Collectors.joining("\n"));

        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при получении задач.";
        }
    }
}
