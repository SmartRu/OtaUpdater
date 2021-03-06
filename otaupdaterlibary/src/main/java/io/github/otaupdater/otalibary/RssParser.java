package io.github.otaupdater.otalibary;

import android.support.annotation.Nullable;
import android.util.Log;

import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import io.github.otaupdater.otalibary.objects.Update;

class RssParser {
    private URL rssUrl;

    public RssParser(String url) {
        try {
            this.rssUrl = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public Update parse() {
        SAXParserFactory factory = SAXParserFactory.newInstance();

        InputStream inputStream = null;

        try {
            URLConnection connection = rssUrl.openConnection();
            inputStream = connection.getInputStream();
            SAXParser parser = factory.newSAXParser();
            RssHandler handler = new RssHandler();
            parser.parse(inputStream, handler);
            return handler.getUpdate();
        } catch (ParserConfigurationException | SAXException e) {
            Log.e("RomUpdater", "The XML updater file is mal-formatted. AppUpdate can't check for updates.", e);
            return null;
        } catch (FileNotFoundException | UnknownHostException | ConnectException e) {
            Log.e("RomUpdater", "The XML updater file is invalid or is down. AppUpdate can't check for updates.");
            return null;
        } catch (IOException e) {
            Log.e("RomUpdater", "I/O error. AppUpdate can't check for updates.", e);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e("RomUpdater", "Error closing input stream", e);
                }
            }
        }

    }

}
