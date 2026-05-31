package com.viniciuspadovam.tcquatro.weeklyreport;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;

public class WeeklyReportHandler implements RequestHandler<ScheduledEvent, String> {

    private final ReportService reportService;

    public WeeklyReportHandler() {
        this(new ReportService());
    }

    public WeeklyReportHandler(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public String handleRequest(ScheduledEvent event, Context context) {
        String result = reportService.processWeeklyReport();

        if (context != null) {
            context.getLogger().log(result);
        }

        return result;
    }
}
