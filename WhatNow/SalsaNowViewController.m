//
//  SalsaNowViewController.m
//  WhatNow
//
//  Created by Victa Phu on 15/06/13.
//  Copyright (c) 2013 __MyCompanyName__. All rights reserved.
//

#import "SalsaNowViewController.h"

#import <GoogleMaps/GoogleMaps.h>
#import <CoreLocation/CoreLocation.h>

@implementation SalsaNowViewController {
    GMSMapView *mapView_;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    responseData = [NSMutableData data];
    
    // Note to Victa: We would perform our checks here to find a users' current location
    // Use current location and a 10 KM radius to find out what's around them
    NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:@"http://connectwf.appspot.com/events.json"]];
    [[NSURLConnection alloc] initWithRequest:request delegate:self];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated
{
	[super viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
	[super viewDidDisappear:animated];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation != UIInterfaceOrientationPortraitUpsideDown);
}

- (void)loadView {
    // Create a GMSCameraPosition that tells the map to display the
    // coordinate -33.86,151.20 at zoom level 6.
    GMSCameraPosition *camera = [GMSCameraPosition cameraWithLatitude:-33.86
                                                            longitude:151.20
                                                                 zoom:14];
    mapView_ = [GMSMapView mapWithFrame:CGRectZero camera:camera];
    mapView_.myLocationEnabled = YES;
    self.view = mapView_;
    
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
	[responseData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
	[responseData appendData:data];
    
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error happened :O" message:@"An Error Occurred, zomg" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:nil];
    // optional - add more buttons:
    [alert addButtonWithTitle:@"Yes"];
    [alert show];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
    NSError *err = nil;
    
    NSArray *jsonArray = [NSJSONSerialization JSONObjectWithData: responseData options:NSJSONReadingMutableContainers error:&err];
    
    if (!jsonArray){
        NSLog(@"Error parsing JSONL %@", err);
    }
    else{
        double lon = 0;
        double lat = 0;
        for(NSDictionary *item in jsonArray)
        {
            lat = [[item objectForKey:@"latitude"] doubleValue];
            lon = [[item objectForKey:@"longtitude"] doubleValue];
            GMSMarker *marker = [[GMSMarker alloc] init];
            marker.position = CLLocationCoordinate2DMake(lat, lon);
            marker.title = [item objectForKey:@"event_name"];
            marker.snippet = @"Australia";
            marker.map = mapView_;
            
        }
        // i should use the user's actual location to centre the map!
        // perhaps i should also indicate where a user is on the map?
        if (lon != 0 && lat != 0)
        {
            GMSCameraPosition *sydney = [GMSCameraPosition cameraWithLatitude:lat
                                                                    longitude:lon
                                                                         zoom:14];
            [mapView_ setCamera:sydney];
        }
    }
}

@end
