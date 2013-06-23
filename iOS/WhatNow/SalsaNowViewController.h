//
//  SalsaNowViewController.h
//  WhatNow
//
//  Created by Victa Phu on 15/06/13.
//  Copyright (c) 2013 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

#import <GoogleMaps/GoogleMaps.h>

@interface SalsaNowViewController : UIViewController <GMSMapViewDelegate>{
    NSMutableData *responseData;
    NSString *myUniqueId;
}

-(IBAction)loginSelected:(id)sender;

-(IBAction)browseSelected:(id)sender;

@end
