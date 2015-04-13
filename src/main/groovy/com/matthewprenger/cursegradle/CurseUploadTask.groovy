package com.matthewprenger.cursegradle

import com.matthewprenger.cursegradle.jsonresponse.CurseError
import com.matthewprenger.cursegradle.jsonresponse.UploadResponse
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.HttpClientBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class CurseUploadTask extends DefaultTask {

    private static final Logger log = Logging.getLogger(CurseUploadTask)


    @Input
    String apiKey

    @Input
    String projectId

    @Input
    CurseArtifact mainArtifact

    @Input
    Collection<CurseArtifact> additionalArtifacts

    @TaskAction
    run() {

        if (''.equals(apiKey)) {
            throw new RuntimeException("Project $projectId apiKey was not set")
        }

        mainArtifact.resolve(project)

        CurseVersions.initialize(apiKey)
        mainArtifact.gameVersions = CurseVersions.resolveGameVersion(mainArtifact.gameVersionStrings)

        final String json = Util.gson.toJson(mainArtifact)
        int mainID = uploadFile(json, (File) mainArtifact.artifact)

        additionalArtifacts.each { artifact ->
            artifact.resolve(project)
            artifact.parentFileID = mainID
            final String childJson = Util.gson.toJson(artifact)
            uploadFile(childJson, (File) artifact.artifact)
        }
    }

    int uploadFile(String json, File file) throws IOException, URISyntaxException {

        if (project.extensions.getByName(CurseGradlePlugin.EXTENSION_NAME).debug) {
            logger.lifecycle("DEBUG: File: $file  Json: $json")
            return 0
        }

        int fileID
        final String uploadUrl = String.format(CurseGradlePlugin.UPLOAD_URL, projectId)
        log.info("Uploading file: {} to url: {} with json: {}", file, uploadUrl, json)

        HttpClient client = HttpClientBuilder.create().build()
        HttpPost post = new HttpPost(new URI(uploadUrl))

        post.addHeader('X-Api-Token', apiKey)
        post.setEntity(MultipartEntityBuilder.create()
                               .addTextBody('metadata', json)
                               .addBinaryBody('file', file)
                               .build())

        HttpResponse response = client.execute(post)

        if (response.statusLine.statusCode == 200) {
            InputStreamReader reader = new InputStreamReader(response.entity.content)
            UploadResponse curseResponse = Util.gson.fromJson(reader, UploadResponse)
            reader.close()
            fileID = curseResponse.id
        } else {
            if (response.getFirstHeader('content-type').value.contains('json')) {
                InputStreamReader reader = new InputStreamReader(response.entity.content)
                CurseError error = Util.gson.fromJson(reader, CurseError)
                reader.close()
                throw new RuntimeException("CurseForge Error: $error.errorCode: $error.errorMessage")
            } else {
                throw new RuntimeException("Error: $response.statusLine.statusCode: $response.statusLine.reasonPhrase")
            }
        }

        log.lifecycle "Uploaded {} to CurseForge Project: {}, with ID: {}", file, projectId, fileID
        return fileID
    }
}
