package com.microsoft.azure.storage;

import com.microsoft.azure.storage.blob.*;
import com.microsoft.azure.storage.blob.Base64;
import com.microsoft.azure.storage.models.*;
import com.microsoft.rest.v2.RestResponse;
import com.microsoft.rest.v2.http.*;
import com.microsoft.rest.v2.util.FlowableUtil;
import io.reactivex.Flowable;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertArrayEquals;


public class BlobStorageAPITests {

    @Test
    public void TestPutBlobBasic() throws IOException, InvalidKeyException, InterruptedException {
        /**
         * This library uses the Azure Rest Pipeline to make its requests. Details on this pipeline can be found here:
         * https://github.com/Azure/azure-pipeline-go/blob/master/pipeline/doc.go All references to HttpPipeline and
         * the like refer to this structure.
         * This library uses Microsoft AutoRest to generate the protocol layer off of the Swagger API spec of the
         * blob service. All files in the implementation and models folders as well as the Interfaces in the root
         * directory are auto-generated using this tool.
         * This library's paradigm is centered around the URL object. A URL is constructed to a resource, such as
         * BlobURL. This is solely a reference to a location; the existence of a BlobURL does not indicate the existence
         * of a blob or hold any state related to the blob. The URL objects define methods for all operations related
         * to that resource (or will eventually; some are not supported in the library yet).
         * Several structures are defined on top of the auto-generated protocol layer to logically group items or
         * concepts relevant to a given operation or resource. This both reduces the length of the parameter list
         * and provides some coherency and relationship of ideas to aid the developer, improving efficiency and
         * discoverability.
         * In this sample test, we demonstrate the use of all APIs that are currently implemented. They have been tested
         * to work in these cases, but they have not been thoroughly tested. More advanced operations performed by
         * specifying or modifying calls in this test are not guaranteed to work. APIs not shown here are not guaranteed
         * to work. Any reports on bugs found will be welcomed and addressed.
         */


        // Creating a pipeline requires a credentials object and a structure of pipeline options to customize the behavior.
        // Set your system environment variables of ACCOUNT_NAME and ACCOUNT_KEY to pull the appropriate credentials.
        // Credentials may be SharedKey as shown here or Anonymous as shown below.
        SharedKeyCredentials creds = new SharedKeyCredentials(System.getenv().get("ACCOUNT_NAME"),
                System.getenv().get("ACCOUNT_KEY"));

        // Currently only the default PipelineOptions are supported.
        HttpPipeline pipeline = StorageURL.createPipeline(creds, new PipelineOptions());

        // Create a reference to the service.
        ServiceURL su = new ServiceURL(
                new URL("http://" + System.getenv().get("ACCOUNT_NAME") + ".blob.core.windows.net"), pipeline);

        // Create a reference to a container. Using the ServiceURL to create the ContainerURL appends
        // the container name to the ServiceURL. A ContainerURL may also be created by calling its
        // constructor with a full path to the container and a pipeline.
        String containerName = "javatestcontainer" + System.currentTimeMillis();
        ContainerURL cu = su.createContainerURL(containerName);

        // Create a reference to a blob. Same pattern as containers.
        BlockBlobURL bu = cu.createBlockBlobURL("javatestblob");
        try {
            // Calls to blockingGet force the call to be synchronous. This whole test is synchronous.
            // APIs will typically return a RestResponse<*HeadersType*, *BodyType*>. It is therefore possible to
            // retrieve the headers and the deserialized body of every request. If there is no body in the request,
            // the body type will be Void.
            // Errors are thrown as exceptions in the synchronous (blockingGet) case.

            // Create the container. NOTE: Metadata is not currently supported on any resource.
            cu.create(null, PublicAccessType.BLOB).blockingGet();

            // List the containers in the account.
            List<Container> containerList = new ArrayList<>();
            String marker = null;
            do {
                RestResponse<ServiceListContainersHeaders, ListContainersResponse> resp = su.listContainers(
                        marker, new ListContainersOptions(null, "java", null)).blockingGet();
                containerList.addAll(resp.body().containers());
                marker = resp.body().marker();
            } while(marker != null);

            // NOTE: Assert statements are only for test purposes and should not be used in production.
            Assert.assertEquals(1, containerList.size());
            Assert.assertEquals(containerList.get(0).name(), containerName);

            // Create the blob with a single put. See below for the putBlock(List) scenario.
            bu.putBlob(Flowable.just(ByteBuffer.wrap(new byte[]{0, 0, 0})), 3, null,
                    null,null).blockingGet();

            // Download the blob contents.
            Flowable<ByteBuffer> data = bu.getBlob(new BlobRange(0L, 3L),
                    null, false).blockingGet().body();
            byte[] dataByte = FlowableUtil.collectBytesInArray(data).blockingGet();
            assertArrayEquals(dataByte, new byte[]{0, 0, 0});

            // Set and retrieve the blob properties. Metadata is not yet supported.
            BlobHttpHeaders headers = new BlobHttpHeaders("myControl", "myDisposition",
                    "myContentEncoding", "myLanguage", null,
                    "myType");
            bu.setProperties(headers, null).blockingGet();
            BlobGetPropertiesHeaders receivedHeaders = bu.getPropertiesAndMetadata(
                    null).blockingGet().headers();
            Assert.assertEquals(headers.getCacheControl(), receivedHeaders.cacheControl());
            Assert.assertEquals(headers.getContentDisposition(), receivedHeaders.contentDisposition());
            Assert.assertEquals(headers.getContentEncoding(), receivedHeaders.contentEncoding());
            Assert.assertEquals(headers.getContentLanguage(), receivedHeaders.contentLanguage());
            Assert.assertEquals(headers.getContentType(), receivedHeaders.contentType());

            // Create a snapshot of the blob and pull the snapshot ID out of the headers.
            String snapshot = bu.createSnapshot(null, null).blockingGet()
                    .headers().snapshot().toString();

            // Create a reference to the blob snapshot. This returns a new BlockBlobURL object that references the same
            // path as the base blob with the query string including the snapshot value appended to the end.
            BlockBlobURL buSnapshot = bu.withSnapshot(snapshot);

            // Download the contents of the snapshot.
            data = buSnapshot.getBlob(new BlobRange(0L, 3L),
                    null, false).blockingGet().body();
            dataByte = FlowableUtil.collectBytesInArray(data).blockingGet();
            assertArrayEquals(dataByte, new byte[]{0,0,0});

            // Create a reference to another blob within the same container and copies the first blob into this location.
            BlockBlobURL bu2 = cu.createBlockBlobURL("javablob2");
            bu2.startCopy(bu.toURL(), null, null, null)
                    .blockingGet();

            // Simple delay to wait for the copy. Inefficient buf effective. A better method would be to periodically
            // poll the blob.
            TimeUnit.SECONDS.sleep(5);

            // Check the existence of the copied blob.
            receivedHeaders = bu2.getPropertiesAndMetadata(null).blockingGet()
                    .headers();
            Assert.assertEquals(headers.getContentType(), receivedHeaders.contentType());

            // Create a reference to a new blob within the same container to upload blocks. Upload a single block.
            BlockBlobURL bu3 = cu.createBlockBlobURL("javablob3");
            ArrayList<String> blockIDs = new ArrayList<>();
            blockIDs.add(Base64.encode(new byte[]{0}));
            bu3.putBlock(blockIDs.get(0), Flowable.just(ByteBuffer.wrap(new byte[]{0,0,0})), 3,
                    null).blockingGet();

            // Get the list of blocks on this blob. For demonstration purposes.
            BlockList blockList = bu3.getBlockList(BlockListType.ALL, null)
                    .blockingGet().body();
            Assert.assertEquals(blockIDs.get(0), blockList.uncommittedBlocks().get(0).name());

            // Get a list of blobs in the container including copies, snapshots, and uncommitted blobs.
            // For demonstration purposes.
            List<Blob> blobs = cu.listBlobs(null,
                    new ListBlobsOptions(new BlobListingDetails(
                            true, false, true, true),
                            null, null, null)).blockingGet().body().blobs().blob();
            Assert.assertEquals(4, blobs.size());

            // Commit the list of blocks. Download the blob to verify.
            bu3.putBlockList(blockIDs, null, null, null).blockingGet();
            data = bu3.getBlob(new BlobRange(0L, 3L),
                    null, false).blockingGet().body();
            dataByte = FlowableUtil.collectBytesInArray(data).blockingGet();
            assertArrayEquals(dataByte, new byte[]{0,0,0});

            // SAS -----------------------------
            // Parses a URL into its constituent components. This structure's URL fields may be modified.
            BlobURLParts parts = URLParser.parse(bu.toURL());

            // Construct the AccountSasSignatureValues values object. This encapsulates all the values needed to create an AccountSasSignatureValues.
            AccountSasSignatureValues sas = new AccountSasSignatureValues();
            AccountSASPermission perms = new AccountSASPermission();
            perms.read = true;
            perms.write = true;
            AccountSASService service = new AccountSASService();
            service.blob = true;
            AccountSASResourceType resourceType = new AccountSASResourceType();
            resourceType.object = true;
            sas.version = "2016-05-31";
            sas.protocol = SASProtocol.HTTPS_HTTP;
            sas.startTime  = null;
            sas.expiryTime= DateTime.now().plusDays(1).toDate();
            sas.permissions = perms.toString();
            sas.ipRange = null;
            sas.services = service.toString();
            sas.resourceTypes = resourceType.toString();

            // Construct a ServiceSasSignatureValues in a pattern similar to that of the AccountSasSignatureValues.
            // Comment out the AccountSasSignatureValues creation and uncomment this to run with ServiceSasSignatureValues.
            /*ServiceSasSignatureValues sas = new ServiceSasSignatureValues("2016-05-31", SASProtocol.HTTPS_HTTP,
                    DateTime.now().minusDays(1).toDate(), DateTime.now().plusDays(1).toDate(),
                    EnumSet.of(ContainerSASPermission.READ, ContainerSASPermission.WRITE),
                    null, containerName, null, null,
                    null, null, null, null);*/


            // GenerateSASQueryParameters hashes the sas using your account's credentials and then associates the
            // sasQueryParameters with the blobURLParts.
            parts.setSasQueryParameters(sas.GenerateSASQueryParameters(creds));

            // Using a SAS requires AnonymousCredentials on the pipeline.
            pipeline = StorageURL.createPipeline(new AnonymousCredentials(), new PipelineOptions());

            // Call toURL on the parts to get a string representation of the URL. This, along with the pipeline,
            // is used to create a new BlockBlobURL object.
            BlockBlobURL sasBlob = new BlockBlobURL(parts.toURL(), pipeline);

            // Download the blob using the SAS. To perform other operations, ensure the appropriate permissions are
            // specified above.
            data = sasBlob.getBlob(new BlobRange(0L, 3L), null, false).blockingGet().body();
            dataByte = FlowableUtil.collectBytesInArray(data).blockingGet();
            assertArrayEquals(dataByte, new byte[]{0, 0, 0});

            // --------------APPEND BLOBS-------------
            AppendBlobURL abu = cu.createAppendBlobURL("appendblob");
            abu.create(null, null, null).blockingGet();
            abu.appendBlock(Flowable.just(ByteBuffer.wrap(new byte[]{0,0,0})), 3,  null).blockingGet();

            data = abu.getBlob(new BlobRange(0L, 3L), null, false).blockingGet().body();
            dataByte = FlowableUtil.collectBytesInArray(data).blockingGet();
            assertArrayEquals(dataByte, new byte[]{0, 0, 0});

            // ---------------PAGE BLOBS-------------
            PageBlobURL pbu = cu.createPageBlobURL("pageblob");
            pbu.create((512L * 3L), null, null, null, null).blockingGet();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            for(int i=0; i<1024; i++) {
                os.write(1);
            }
            pbu.putPages(new PageRange().withStart(0).withEnd(1023), Flowable.just(ByteBuffer.wrap(os.toByteArray())),
                    null).blockingGet();
            String pageSnap = pbu.createSnapshot(null, null).blockingGet().headers().snapshot();
            pbu.clearPages(new PageRange().withStart(0).withEnd(511), null).blockingGet();
            PageRange pr = pbu.getPageRanges(new BlobRange(0L, (512L * 3L)), null).blockingGet()
                    .body().pageRange().get(0);
            Assert.assertEquals(pr.start(), 512);
            Assert.assertEquals(pr.end(), 1023);
            ClearRange cr = pbu.getPageRangesDiff(null, pageSnap, null).blockingGet().body().clearRange().get(0);
            Assert.assertEquals(cr.start(), 0);
            Assert.assertEquals(cr.end(), 511);

            pbu.resize(512L * 4L, null).blockingGet();
            pbu.setSequenceNumber(SequenceNumberActionType.INCREMENT, null, null, null).blockingGet();
            BlobGetPropertiesHeaders pageHeaders = pbu.getPropertiesAndMetadata(null).blockingGet().headers();
            Assert.assertEquals(1, pageHeaders.blobSequenceNumber().longValue());
            Assert.assertEquals((long)(512*4), pageHeaders.contentLength().longValue());

            PageBlobURL copyPbu = cu.createPageBlobURL("copyPage");
            CopyStatusType status = copyPbu.startIncrementalCopy(pbu.toURL(), pageSnap, null).blockingGet().headers().copyStatus();
            Assert.assertEquals(CopyStatusType.PENDING, status);

            // ACCOUNT----------------------------
            StorageServiceProperties props = new StorageServiceProperties();
            Logging logging = new Logging().withRead(true).withVersion("1.0").
                    withRetentionPolicy(new RetentionPolicy().withDays(1).withEnabled(true));
            props = props.withLogging(logging);
            su.setProperties(props).blockingGet();

            StorageServiceProperties receivedProps = su.getProperties().blockingGet().body();
            Assert.assertEquals(receivedProps.logging().read(), props.logging().read());

            su.setProperties(props.withLogging(logging.withRead(false).withRetentionPolicy(new RetentionPolicy()
                    .withEnabled(false)))).blockingGet();

            String secondaryAccount = System.getenv("ACCOUNT_NAME") + "-secondary";
            pipeline = StorageURL.createPipeline(creds, new PipelineOptions());
            ServiceURL secondary = new ServiceURL(new URL("http://" + secondaryAccount + ".blob.core.windows.net"),
                    pipeline);
            secondary.getStats().blockingGet();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            // Delete the blob and container. Deleting a container does not require deleting the blobs first.
            // This is just for demonstration purposes.
            try {
                bu.delete(DeleteSnapshotsOptionType.INCLUDE, null).blockingGet();
            }
            finally {
                cu.delete(null).blockingGet();
            }
        }
    }

}
