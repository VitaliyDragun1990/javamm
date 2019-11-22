/*
 *
 *  Copyright (c) 2019. http://devonline.academy
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.revenat.javamm.ide.util;

import com.revenat.javamm.code.exception.ConfigException;

import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;

/**
 * @author Vitaliy Dragun
 */
public final class ResourceUtils {

    private ResourceUtils() {
    }

    /**
     * Returns {@linkplain URL url} for specified {@code classpathResourceName}
     *
     * @param classpathResourceName class path to requested resource
     * @return {@linkplain URL url} for class path resource
     */
    public static URL getClassPathResource(final String classpathResourceName) {
        final URL url = ResourceUtils.class.getResource(classpathResourceName);
        if (url == null) {
            throw new ConfigException("Class path resource not found: " + classpathResourceName);
        }
        return url;
    }

    /**
     * Loads data from {@code *.fxml} resource, making provided {@code component} both {@code root}
     * and {@code controller} for specified resource
     *
     * @param component component into which data will be loaded
     * @param resource  {@code *.fxml} resource to load data from
     */
    public static void loadFromFxmlResource(final Object component, final String resource) {
        FXMLLoader fxmlLoader = createLoader(component, resource);
        loadResource(fxmlLoader);
    }

    private static FXMLLoader createLoader(final Object component, final String resource) {
        FXMLLoader fxmlLoader = new FXMLLoader(component.getClass().getResource(resource));
        fxmlLoader.setRoot(component);
        fxmlLoader.setController(component);
        return fxmlLoader;
    }

    private static void loadResource(final FXMLLoader fxmlLoader) {
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
