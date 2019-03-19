## On iOS devices

### What should the format of the Base64 string be?

Look at the following code below:

```ObjectiveC
/**
 * Returns the data for a given (relative) attachment path.
 *
 * @param path An absolute/relative path or the base64 data
 *
 * @return The data for the attachment.
 */
- (NSData*) getDataForAttachmentPath:(NSString*)path
{
    if ([path hasPrefix:@"file:///"])
    {
        return [self dataForAbsolutePath:path];
    }
    else if ([path hasPrefix:@"res:"])
    {
        return [self dataForResource:path];
    }
    else if ([path hasPrefix:@"file://"])
    {
        return [self dataForAsset:path];
    }
    else if ([path hasPrefix:@"app://"])
    {
        return [self dataForAppInternalPath:path];
    }
    else if ([path hasPrefix:@"base64:"])
    {
        return [self dataFromBase64:path];
    }

    NSFileManager* fm = [NSFileManager defaultManager];

    if (![fm fileExistsAtPath:path]){
        NSLog(@"File not found: %@", path);
    }

    return [fm contentsAtPath:path];
}
```

The following code recommends that the base 64 representation of the file to be attached must begin with the prefix `base64:data` where data is your data represented in base64.
