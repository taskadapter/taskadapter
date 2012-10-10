package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTaskDescriptor;

class DefaultMSPMappings {
    static Mappings generate() {
        final Mappings result = new Mappings();
        result.setMapping(GTaskDescriptor.FIELD.SUMMARY, true, null);
        result.setMapping(GTaskDescriptor.FIELD.TASK_TYPE, true, MSPUtils.getDefaultTaskType());

        // TODO set default values in MSP..Provider instead of using [0]
        String defaultEstimatedTimeOption = MSPUtils.getEstimatedTimeOptions()[0];
        result.setMapping(GTaskDescriptor.FIELD.ESTIMATED_TIME, true, defaultEstimatedTimeOption);
        result.setMapping(GTaskDescriptor.FIELD.DONE_RATIO, true, null);
        result.setMapping(GTaskDescriptor.FIELD.ASSIGNEE, true, null);
        result.setMapping(GTaskDescriptor.FIELD.DESCRIPTION, true, null);

        String defaultStartDateOption = MSPUtils.getStartDateOptions()[0];
        result.setMapping(GTaskDescriptor.FIELD.START_DATE, false, defaultStartDateOption);

        String defaultDueDateOption = MSPUtils.getDueDateOptions()[0];
        result.setMapping(GTaskDescriptor.FIELD.DUE_DATE, false, defaultDueDateOption);

        /*
         *  when "saveRemoteId" was by default TRUE for new configs,
         *  many users have complained that they exported tasks from an MSP file
         *  to Redmine, then deleted them in the Redmine and tried to re-export
         *  and got "issue with id... not found".
         *  it's better to have this option set to FALSE by default to avoid the confusion.
         */
        result.setMapping(GTaskDescriptor.FIELD.REMOTE_ID, false, MSPUtils.getDefaultRemoteIdMapping());
        result.setMapping(GTaskDescriptor.FIELD.TASK_STATUS, false, MSPUtils.getDefaultTaskStatus());
        return result;
    }
}
