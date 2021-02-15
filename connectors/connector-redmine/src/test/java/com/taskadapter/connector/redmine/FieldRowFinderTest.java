package com.taskadapter.connector.redmine;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.model.Priority$;
import com.taskadapter.model.Summary$;
import org.junit.Test;
import scala.Option;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldRowFinderTest {
    @Test
    public void found() {
        List<FieldRow<?>> rows = List.of(
                new FieldRow<>(
                        Option.apply(Summary$.MODULE$),
                        Option.apply(Summary$.MODULE$),
                        "default"
                ),
                new FieldRow<>(
                        Option.apply(Priority$.MODULE$),
                        Option.apply(Priority$.MODULE$),
                        "default"
                ));
        assertThat(FieldRowFinder.containsTargetField(rows, Priority$.MODULE$))
                .isTrue();
    }

    @Test
    public void notFound() {
        List<FieldRow<?>> rows = List.of(new FieldRow<>(
                Option.apply(Summary$.MODULE$),
                Option.apply(Summary$.MODULE$),
                "default"
        ));
        assertThat(FieldRowFinder.containsTargetField(rows, Priority$.MODULE$))
                .isFalse();
    }

}