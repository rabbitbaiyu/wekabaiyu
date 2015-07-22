/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    TrainingSetMaker.java
 *    Copyright (C) 2002 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.gui.beans;

import java.io.Serializable;
import java.util.Vector;

/**
 * Bean that accepts a data sets and produces a training set
 * 
 * @author <a href="mailto:mhall@cs.waikato.ac.nz">Mark Hall</a>
 * @version $Revision: 9751 $
 */
public class TrainingSetMaker extends AbstractTrainingSetProducer implements
    DataSourceListener, TestSetListener, EventConstraints, Serializable {

  /** for serialization */
  private static final long serialVersionUID = -6152577265471535786L;

  protected boolean m_receivedStopNotification = false;

  public TrainingSetMaker() {
    m_visual.loadIcons(BeanVisual.ICON_PATH + "TrainingSetMaker.gif",
        BeanVisual.ICON_PATH + "TrainingSetMaker_animated.gif");
    m_visual.setText("TrainingSetMaker");
  }

  /**
   * Set a custom (descriptive) name for this bean
   * 
   * @param name the name to use
   */
  @Override
  public void setCustomName(String name) {
    m_visual.setText(name);
  }

  /**
   * Get the custom (descriptive) name for this bean (if one has been set)
   * 
   * @return the custom name (or the default name)
   */
  @Override
  public String getCustomName() {
    return m_visual.getText();
  }

  /**
   * Global info for this bean
   * 
   * @return a <code>String</code> value
   */
  public String globalInfo() {
    return Messages.getInstance().getString("TrainingSetMaker_GlobalInfo_Text");
  }

  /**
   * Accept a data set
   * 
   * @param e a <code>DataSetEvent</code> value
   */
  @Override
  public void acceptDataSet(DataSetEvent e) {
    m_receivedStopNotification = false;
    TrainingSetEvent tse = new TrainingSetEvent(this, e.getDataSet());
    tse.m_setNumber = 1;
    tse.m_maxSetNumber = 1;
    notifyTrainingSetProduced(tse);
  }

  @Override
  public void acceptTestSet(TestSetEvent e) {
    m_receivedStopNotification = false;
    TrainingSetEvent tse = new TrainingSetEvent(this, e.getTestSet());
    tse.m_setNumber = 1;
    tse.m_maxSetNumber = 1;
    notifyTrainingSetProduced(tse);
  }

  /**
   * Inform training set listeners that a training set is available
   * 
   * @param tse a <code>TrainingSetEvent</code> value
   */
  protected void notifyTrainingSetProduced(TrainingSetEvent tse) {
    Vector l;
    synchronized (this) {
      l = (Vector) m_listeners.clone();
    }
    if (l.size() > 0) {
      for (int i = 0; i < l.size(); i++) {
        if (m_receivedStopNotification) {
          if (m_logger != null) {
            m_logger
                .logMessage(Messages
                    .getInstance()
                    .getString(
                        "TrainingSetMaker_NotifyTrainingSetProduced_LogMessage_Text_First")
                    + statusMessagePrefix()
                    + Messages
                        .getInstance()
                        .getString(
                            "TrainingSetMaker_NotifyTrainingSetProduced_LogMessage_Text_Second"));
            m_logger
                .statusMessage(statusMessagePrefix()
                    + Messages
                        .getInstance()
                        .getString(
                            "TrainingSetMaker_NotifyTrainingSetProduced_LogMessage_Text_Third"));
          }
          m_receivedStopNotification = false;
          break;
        }
        System.err.println(Messages.getInstance().getString(
            "TrainingSetMaker_NotifyTrainingSetProduced_Error_Text"));
        ((TrainingSetListener) l.elementAt(i)).acceptTrainingSet(tse);
      }
    }
  }

  /**
   * Stop any action
   */
  @Override
  public void stop() {
    m_receivedStopNotification = true;

    // tell the listenee (upstream bean) to stop
    if (m_listenee instanceof BeanCommon) {
      // System.err.println("Listener is BeanCommon");
      ((BeanCommon) m_listenee).stop();
    }
  }

  /**
   * Returns true if. at this time, the bean is busy with some (i.e. perhaps a
   * worker thread is performing some calculation).
   * 
   * @return true if the bean is busy.
   */
  @Override
  public boolean isBusy() {
    return false;
  }

  /**
   * Returns true, if at the current time, the named event could be generated.
   * Assumes that supplied event names are names of events that could be
   * generated by this bean.
   * 
   * @param eventName the name of the event in question
   * @return true if the named event could be generated at this point in time
   */
  @Override
  public boolean eventGeneratable(String eventName) {
    if (m_listenee == null) {
      return false;
    }

    if (m_listenee instanceof EventConstraints) {
      if (!((EventConstraints) m_listenee).eventGeneratable("dataSet")) {
        return false;
      }
    }
    return true;
  }

  private String statusMessagePrefix() {
    return getCustomName() + "$" + hashCode() + "|";
  }
}
