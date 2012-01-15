package com.taskadapter.config;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.taskadapter.PluginManager;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class TAConfigTest {

  @Test
  public void cloneConstructor() throws IOException {
    URL resource = Resources.getResource("redmine.ta_conf");
    String configFileContents = Resources.toString(resource, Charsets.UTF_8);
    ConfigFileParser parser = new ConfigFileParser(new PluginManager());
    TAConfig config = parser.parse(configFileContents);

    TAConfig cloned = new TAConfig(config);

    assertEquals(config, cloned);
  }
}
