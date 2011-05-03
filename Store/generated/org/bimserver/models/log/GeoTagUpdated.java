/**
 *  (c) Copyright bimserver.org 2009
 *  Licensed under GNU GPLv3
 *  http://www.gnu.org/licenses/gpl-3.0.txt
 *  For more information mail to license@bimserver.org
 *  
 *  Bimserver.org is free software: you can redistribute it and/or modify 
 *  it under the terms of the GNU General Public License as published by 
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Bimserver.org is distributed in the hope that it will be useful, but 
 *  WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 *  General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License a 
 *  long with Bimserver.org . If not, see <http://www.gnu.org/licenses/>.
 */
package org.bimserver.models.log;

import org.bimserver.models.log.LogAction;

import org.bimserver.models.store.GeoTag;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Geo Tag Updated</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.bimserver.models.log.GeoTagUpdated#getGeoTag <em>Geo Tag</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.bimserver.models.log.LogPackage#getGeoTagUpdated()
 * @model
 * @generated
 */
public interface GeoTagUpdated extends LogAction
{
	/**
	 * Returns the value of the '<em><b>Geo Tag</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Geo Tag</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Geo Tag</em>' reference.
	 * @see #setGeoTag(GeoTag)
	 * @see org.bimserver.models.log.LogPackage#getGeoTagUpdated_GeoTag()
	 * @model
	 * @generated
	 */
	GeoTag getGeoTag();

	/**
	 * Sets the value of the '{@link org.bimserver.models.log.GeoTagUpdated#getGeoTag <em>Geo Tag</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Geo Tag</em>' reference.
	 * @see #getGeoTag()
	 * @generated
	 */
	void setGeoTag(GeoTag value);

} // GeoTagUpdated
